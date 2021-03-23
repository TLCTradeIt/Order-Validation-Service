package com.example.ordervalidationservice.soapservice;

import com.example.ordervalidationservice.MarketData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Endpoint
public class ReceiveOrderEndpoint {

    @Autowired
    private WebClient.Builder webClient;

    @PayloadRoot(namespace = "http://ordervalidationservice.example.com/soapservice", localPart = "SendOrderRequest")
    @ResponsePayload
    public SendOrderResponse processClientConnectivityRequest(@RequestPayload SendOrderRequest request){
        SendOrderResponse response = new SendOrderResponse();

        Long orderId = request.getOrder().getOrderId();
        response.setOrderId(orderId);
        response.setIsValidated(false);
        response.setStatus("Rejected");

        // market data
        MarketData marketData = getMarketData(request.getOrder().getProduct().getTicker());

        // Order Validation
        if (request.getOrder().getSide().equals("Buy")){
            Boolean isBalanceValid = validateAccBalance(request.getOrder().getClient().getAccBalance(), request.getOrder().getPrice());
            if(isBalanceValid){
                Boolean isBuyPriceValid = validateBuyPrice(request.getOrder().getPrice(), marketData.getBID_PRICE(), marketData.getMAX_PRICE_SHIFT());
                if(isBuyPriceValid){
                    Boolean isBuyLimitValid = validateBuyLimit(request.getOrder().getQuantity(), marketData.getBUY_LIMIT());
                    if(isBuyLimitValid){
                        response.setIsValidated(true);
                        response.setStatus("Accepted");
                        response.setMessage("Order has been accepted");
                    }
                    else {
                        response.setMessage("Buy quantity limit exceeded");
                    }
                }
                else {
                    response.setMessage("Buying price is too low");
                }
            }else {
                response.setMessage("Insufficient Funds");
            }
        } else {
            Boolean isPresent = checkProductPresenceInPortfolio(request.getOrder().getProduct().getPortfolio().getPortfolioId(), request.getOrder().getPortfolio().getPortfolioId());
            if(isPresent){
                Boolean isSellQuantityValid = validateSellQuantity(request.getOrder().getProduct().getProdQuantity(), request.getOrder().getQuantity());
                if(isSellQuantityValid){
                    Boolean isSellPriceValid = validateSellPrice(request.getOrder().getPrice(), marketData.getASK_PRICE(), marketData.getMAX_PRICE_SHIFT());
                    if(isSellPriceValid){
                        Boolean isSellLimitValid = validateSellLimit(request.getOrder().getQuantity(), marketData.getSELL_LIMIT());
                        if(isSellLimitValid){
                            response.setIsValidated(true);
                            response.setStatus("Accepted");
                            response.setMessage("Order has been accepted");
                        }
                        else {
                            response.setMessage("Sell quantity limit exceeded");
                        }
                    }
                    else {
                        response.setMessage("Selling price is too high");
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

    public Boolean validateAccBalance(Double balance, Double price){
        return balance >= price;
    }

    // ppId - product portfolio Id, opId - order portfolio Id
    public Boolean checkProductPresenceInPortfolio(Long ppId, Long opId){
        // check product portfolioId against order portfolioId, if it matches then product is in portfolio
        return ppId.equals(opId);
    }

    public Boolean validateSellQuantity(Integer productQuantity, Integer orderQuantity){
        return productQuantity >= orderQuantity;
    }

    public Boolean validateBuyPrice(Double orderBuyPrice, Double marketBuyPrice, Double maxPriceShift){
        return orderBuyPrice >= (marketBuyPrice - maxPriceShift);// client's buying price is too low
    }

    public Boolean validateSellPrice(Double orderSellPrice, Double marketSellPrice, Double maxPriceShift){
        return orderSellPrice <= (marketSellPrice + maxPriceShift);// client's selling price is too expensive
    }

    public Boolean validateBuyLimit(Integer buyQuantity, Integer marketBuyLimit){
        return buyQuantity <= marketBuyLimit;
    }

    public Boolean validateSellLimit(Integer sellQuantity, Integer marketSellLimit){
        return sellQuantity <= marketSellLimit;
    }

    public MarketData getMarketData(String ticker){
        return webClient.build()
                .get()
                .uri("https://exchange.matraining.com/md/" + ticker)
                .retrieve()
                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus),
                        clientResponse -> Mono.empty())
                .bodyToMono(MarketData.class)
                .block();
    }
}
