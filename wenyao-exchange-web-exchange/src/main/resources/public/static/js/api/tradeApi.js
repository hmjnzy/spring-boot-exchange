'use strict';
function TradeApi () {
    this.call = function(uri, callback, rf, params) {
        $.ajax({
            url: uri,type: "POST",dataType: 'json',contentType: "application/json; charset=UTF-8",data: JSON.stringify(params)
            , success: function (result) {
                rf(result.data);
                return callback(result);
            }
            , error:function(e){
                var result = new Object();
                result.status = 1000;
                result.message = JSON.parse(e.responseText).message;
                return callback(result);
            }
        });
        return true;
    };
    this.buy = function(callback, params) {return this.call("/api/trade/buy", callback, function(r) {}, params)};
    this.sell = function(callback, params) {return this.call("/api/trade/sell", callback, function(r) {}, params)};
    this.cancel = function(callback, params) {return this.call("/api/trade/cancel", callback, function(r) {}, params)};
    this.getRealTimeOrders = function(callback, params) {return this.call("/api/trade/getRealTimeOrders", callback, function(r) {}, params)};
    this.getMyOrders = function(callback, params) {return this.call("/api/trade/getMyOrders", callback, function(r) {}, params)};
    this.getMarketHistoryOrders = function(callback, params) {return this.call("/api/trade/getMarketHistoryOrders", callback, function(r) {}, params)};
    this.getMyHistoryOrders = function(callback, params) {return this.call("/api/trade/getMyHistoryOrders", callback, function(r) {}, params)};
}
var tradeApi = new TradeApi();