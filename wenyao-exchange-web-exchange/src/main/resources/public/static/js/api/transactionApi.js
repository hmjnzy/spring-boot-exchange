'use strict';
function TransactionApi () {
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
    this.requestWithdrawal = function(callback, params) {return this.call("/api/transaction/requestWithdrawal", callback, function(r) {}, params)};
    this.getTransactions = function(callback, params) {return this.call("/api/transaction/getTransactions", callback, function(r) {}, params)};
}
var transactionApi = new TransactionApi();