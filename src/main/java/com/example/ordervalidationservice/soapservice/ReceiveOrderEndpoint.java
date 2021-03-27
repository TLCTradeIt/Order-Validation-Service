package com.example.ordervalidationservice.soapservice;

import com.example.ordervalidationservice.MarketData;
import com.example.ordervalidationservice.Publishing_To_TE.OrderDto;
import com.example.ordervalidationservice.Publishing_To_TE.models.Client;
import com.example.ordervalidationservice.Publishing_To_TE.models.Portfolio;
import com.example.ordervalidationservice.Publishing_To_TE.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class ReceiveOrderEndpoint {

    @Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	@Autowired
	private RestTemplate restTemplate;


    WebClient webClient = WebClient.create();

    @PayloadRoot(namespace = "http://ordervalidationservice.example.com/soapservice", localPart = "SendOrderRequest")
    @ResponsePayload
    public SendOrderResponse processClientConnectivityRequest(@RequestPayload SendOrderRequest request){
        SendOrderResponse response = new SendOrderResponse();

        Long orderId = request.getOrder().getOrderId();
        response.setOrderId(orderId);
        response.setIsValidated(false);
        response.setStatus("Rejected");

        // market data
        MarketData marketData1 = getMarketData(request.getOrder().getProduct().getTicker(), "exchange");
        MarketData marketData2 = getMarketData(request.getOrder().getProduct().getTicker(), "exchange2");


        // Order Validation
        if (request.getOrder().getSide().equals("Buy")){
            Boolean isBalanceValid = validateAccBalance(request.getOrder().getClient().getAccBalance(), request.getOrder().getPrice(), request.getOrder().getQuantity());
            if(isBalanceValid){
                Boolean isBuyPriceValid = validateBuyPrice(request.getOrder().getPrice(), marketData1.getBid_price(), marketData2.getBid_price(), marketData1.getMax_price_shift());
                if(isBuyPriceValid){
                    Boolean isBuyLimitValid = validateBuyLimit(request.getOrder().getQuantity(), marketData1.getBuy_limit());
                    if(isBuyLimitValid){
                        response.setIsValidated(true);
                        response.setStatus("Accepted");
                        response.setMessage("Order has been accepted");

                       // send to trading engine
                        new Thread(() -> {
                            OrderDto postResponse  = postOrderObject(request);
                            System.out.println(postResponse);
                        }).start();

                    }
                    else {
                        response.setMessage("Buy quantity limit exceeded");
                    }
                }
                else {
                    response.setMessage("Buying price is out of range");
                }
            }else {
                response.setMessage("Insufficient Funds");
            }
        } else {
            Boolean isPresent = checkProductPresenceInPortfolio(request.getOrder().getProduct().getPortfolio().getPortfolioId(), request.getOrder().getPortfolio().getPortfolioId());
            if(isPresent){
                Boolean isSellQuantityValid = validateSellQuantity(request.getOrder().getProduct().getProdQuantity(), request.getOrder().getQuantity());
                if(isSellQuantityValid){
                    Boolean isSellPriceValid = validateSellPrice(request.getOrder().getPrice(), marketData1.getAsk_price(), marketData2.getAsk_price(), marketData1.getMax_price_shift());
                    if(isSellPriceValid){
                        Boolean isSellLimitValid = validateSellLimit(request.getOrder().getQuantity(), marketData1.getSell_limit());
                        if(isSellLimitValid){
                            response.setIsValidated(true);
                            response.setStatus("Accepted");
                            response.setMessage("Order has been accepted");

                            // send to trading engine
                            new Thread(() -> {
                                OrderDto postResponse  = postOrderObject(request);
                                System.out.println(postResponse);
                            }).start();
                        }
                        else {
                            response.setMessage("Sell quantity limit exceeded");
                        }
                    }
                    else {
                        response.setMessage("Selling price is out of range");
                    }
                }
                else {
                    response.setMessage("Insufficient product quantity");
                }
            }
            else {
                response.setMessage("Product does not exist in portfolio");
            }
        }

        return response;
    }

    public Boolean validateAccBalance(Double balance, Double price, Integer quantity){
        return balance >= (price * quantity);
    }

    // ppId - product portfolio Id, opId - order portfolio Id
    public Boolean checkProductPresenceInPortfolio(Long ppId, Long opId){
        // check product portfolioId against order portfolioId, if it matches then product is in portfolio
        return ppId.equals(opId);
    }

    public Boolean validateSellQuantity(Integer productQuantity, Integer orderQuantity){
        return productQuantity >= orderQuantity;
    }

    public Boolean validateBuyPrice(Double orderBuyPrice, Double market1BuyPrice, Double market2BuyPrice, Double maxPriceShift){
        if(market1BuyPrice >= market2BuyPrice){
            return (orderBuyPrice >= (market2BuyPrice - maxPriceShift)  && orderBuyPrice <= (market1BuyPrice + maxPriceShift));
        }
        return (orderBuyPrice >= (market1BuyPrice - maxPriceShift)  && orderBuyPrice <= (market2BuyPrice + maxPriceShift));
    }

    public Boolean validateSellPrice(Double orderSellPrice, Double market1SellPrice, Double market2SellPrice, Double maxPriceShift){
        if(market1SellPrice >= market2SellPrice){
            return (orderSellPrice <= (market1SellPrice + maxPriceShift) && orderSellPrice >= (market2SellPrice - maxPriceShift));
        }
        return (orderSellPrice <= (market2SellPrice + maxPriceShift) && orderSellPrice >= (market1SellPrice - maxPriceShift));
    }

    public Boolean validateBuyLimit(Integer buyQuantity, Integer marketBuyLimit){
        return buyQuantity <= marketBuyLimit;
    }

    public Boolean validateSellLimit(Integer sellQuantity, Integer marketSellLimit){
        return sellQuantity <= marketSellLimit;
    }

    public MarketData getMarketData(String ticker, String exchange){
        return webClient
                .get()
                .uri("https://" + exchange + ".matraining.com/md/" + ticker)
                .retrieve()
                .bodyToMono(MarketData.class)
                .block();
    }

    public OrderDto postOrderObject(SendOrderRequest request){
        // prepping order to be sent
        Portfolio orderPortfolio = new Portfolio(request.getOrder().getPortfolio().getPortfolioId());
        Portfolio productPortfolio = new Portfolio(request.getOrder().getProduct().getPortfolio().getPortfolioId());
        Product product = new Product(request.getOrder().getProduct().getProductId(), request.getOrder().getProduct().getTicker(), request.getOrder().getProduct().getExchange(), request.getOrder().getProduct().getProdQuantity(), productPortfolio);
        Client client = new Client(request.getOrder().getClient().getClientId(), request.getOrder().getClient().getAccBalance());

        OrderDto orderDto = new OrderDto(
                request.getOrder().getOrderId(),
                request.getOrder().getQuantity(),
                request.getOrder().getPrice(),
                request.getOrder().getSide(),
                null,
                product,
                client,
                orderPortfolio);

        // sending accepted order to the trade engine
        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://order-validation.herokuapp.com/publish";
        String url = "http://localhost:5009/publish";
        OrderDto result = restTemplate.postForObject(url , orderDto, OrderDto.class);
        System.out.println(result);

        return result;
    }
}
