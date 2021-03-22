package com.example.ordervalidationservice.soapservice;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ReceiveOrderEndpoint {

    @PayloadRoot(namespace = "http://ordervalidationservice.example.com/soapservice", localPart = "SendOrderRequest")
    @ResponsePayload
    public SendOrderResponse processClientConnectivityRequest(@RequestPayload SendOrderRequest request){
        SendOrderResponse response = new SendOrderResponse();

        Long orderId = request.getOrder().getOrderId();
        response.setOrderId(orderId);
        response.setIsValidated(false);
        response.setStatus("Rejected");


        if (request.getOrder().getSide().equals("Buy")){
            Boolean isValid = validateAccBalance(request.getOrder().getClient().getAccBalance(), request.getOrder().getPrice());
            if(isValid){
                response.setIsValidated(true);
                response.setStatus("Accepted");
                response.setMessage("Order has been accepted");
            }else {
                response.setMessage("Insufficient Funds");
            }
        } else {
            Boolean isPresent = checkProductPresenceInPortfolio(request.getOrder().getProduct().getPortfolio().getPortfolioId(), request.getOrder().getPortfolio().getPortfolioId());
            if(isPresent){
                Boolean isSellQuantityValid = validateSellQuantity(request.getOrder().getProduct().getProdQuantity(), request.getOrder().getQuantity());
                if(isSellQuantityValid){
                    response.setIsValidated(true);
                    response.setStatus("Accepted");
                    response.setMessage("Order has been accepted");
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

}
