package com.example.ordervalidationservice.soapservice;

import com.example.ordervalidationservice.MarketData;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import reactor.core.publisher.Mono;

@Endpoint
public class ReceiveOrderEndpoint {

    @Autowired
    WebClient webClient;

    @PayloadRoot(namespace = "http://ordervalidationservice.example.com/soapservice", localPart = "SendOrderRequest")
    @ResponsePayload
    public SendOrderResponse processClientConnectivityRequest(@RequestPayload SendOrderRequest request){
        SendOrderResponse response = new SendOrderResponse();

        Long orderId = request.getOrder().getOrderId();
        response.setOrderId(orderId);
        response.setIsValidated(false);
        response.setStatus("Rejected");

        // market data
        Mono<MarketData> marketData = getMarketData(request.getOrder().getProduct().getTicker());

        // Order Validation
        if (request.getOrder().getSide().equals("Buy")){
            Boolean isBalanceValid = validateAccBalance(request.getOrder().getClient().getAccBalance(), request.getOrder().getPrice());
            if(isBalanceValid){
                Boolean isBuyPriceValid = validateBuyPrice(request.getOrder().getPrice(), marketData.block().getBID_PRICE(), marketData.block().getMAX_PRICE_SHIFT());
                if(isBuyPriceValid){
                    Boolean isBuyLimitValid = validateBuyLimit(request.getOrder().getQuantity(), marketData.block().getBUY_LIMIT());
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
                    Boolean isSellPriceValid = validateSellPrice(request.getOrder().getPrice(), marketData.block().getASK_PRICE(), marketData.block().getMAX_PRICE_SHIFT());
                    if(isSellPriceValid){
                        Boolean isSellLimitValid = validateSellLimit(request.getOrder().getQuantity(), marketData.block().getSELL_LIMIT());
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
        if (balance >= price){
            return true;
        }
        return false;
    }

    // ppId - product portfolio Id, opId - order portfolio Id
    public Boolean checkProductPresenceInPortfolio(Long ppId, Long opId){
        // check product portfolioId against order portfolioId, if it matches then product is in portfolio
        if(ppId.equals(opId)){
            return true;
        }
        return false;
    }

    public Boolean validateSellQuantity(Integer productQuantity, Integer orderQuantity){
        if(productQuantity >= orderQuantity){
            return true;
        }
        return false;
    }

    public Boolean validateBuyPrice(Double orderBuyPrice, Double marketBuyPrice, Double maxPriceShift){
        if(orderBuyPrice >= (marketBuyPrice - maxPriceShift)){
            return true;
        }
        return false; // client's buying price is too low
    }

    public Boolean validateSellPrice(Double orderSellPrice, Double marketSellPrice, Double maxPriceShift){
        if(orderSellPrice <= (marketSellPrice + maxPriceShift)){
            return true;
        }
        return false; // client's selling price is too expensive
    }

    public Boolean validateBuyLimit(Integer buyQuantity, Integer marketBuyLimit){
        if(buyQuantity <= marketBuyLimit){
            return true;
        }
        return false;
    }

    public Boolean validateSellLimit(Integer sellQuantity, Integer marketSellLimit){
        if(sellQuantity <= marketSellLimit){
            return true;
        }
        return false;
    }

    public Mono<MarketData> getMarketData(String ticker){
        return webClient.get()
                .uri("https://exchange.matraining.com/md/" + ticker)
                .retrieve()
                .onStatus(httpStatus -> HttpStatus.NOT_FOUND.equals(httpStatus),
                        clientResponse -> Mono.empty())
                .bodyToMono(MarketData.class);
    }

}
