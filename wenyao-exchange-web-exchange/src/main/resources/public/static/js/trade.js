'use strict';
function getMyWalletsTradeCoin() {
    walletApi.getMyWalletsTradeCoin(function(r) { //console.log(JSON.stringify(r));
        if (r.status == 0) {

            for (var index in r.data.infos) {

                var info = r.data.infos[index];

                if (info.wallet) {
                    if (index == 0) {
                        $('#fstBaseCoinValue').text(info.wallet.availableBalance);
                        $('#fstBaseCoinLeftValue').text(info.wallet.usingBalance);//使用中
                        $('#walletBaseCoinAvaDisplayFromBuy').text(info.wallet.availableBalance);
                        $('#walletBaseCoinAvaFromBuy').val(info.wallet.availableBalance);
                        $('#walletBaseCoinAvaFromSell').val(info.wallet.availableBalance);
                    } else {
                        $('#secTradeCoinValue').text(info.wallet.availableBalance);
                        $('#secTradeCoinLeftValue').text(info.wallet.usingBalance);//使用中
                        $('#walletTradeCoinAvaDisplayFromSell').text(info.wallet.availableBalance);
                        $('#walletTradeCoinAvaFromBuy').val(info.wallet.availableBalance);
                        $('#walletTradeCoinAvaFromSell').val(info.wallet.availableBalance);
                    }
                }
            }
        } else {
            alert(r.message);
        }
    }, {coinPair: selectedCoinPair});
}

function onReqOrderClick(type) {
    determine(type);
    var form = formToObj(document.getElementById(type + 'Form'));
    if (type === 'buy') {
        tradeApi.buy(function (result) {
            if (result.status == 0) {
                var msg = "";
                if (result.data.tradedOrders != null) {
                    for(var index in result.data.tradedOrders) {
                        var tradedOrder = result.data.tradedOrders[index];
                        msg += "*数量 : " + tradedOrder.amount + ", *价格 : " + tradedOrder.price + " 购买完成." + "\n";
                    }
                } else {
                    msg = result.message;
                }

                alert("购买请求成功" + msg);
                init();
            } else {
                alert("购买请求失败" + result.message);
            }
        }, form);
    } else if (type === 'sell') {
        tradeApi.sell(function(result) {
            if (result.status == 0) {
                var msg = "";
                if (result.data.tradedOrders != null) {
                    for(var index in result.data.tradedOrders) {
                        var tradedOrder = result.data.tradedOrders[index];
                        msg += "*数量 : " + tradedOrder.amount + ", *价格 : " + tradedOrder.price + " 卖完成." + "\n";
                    }
                } else {
                    msg = result.message;
                }

                alert("卖请求成功" + msg);
                init();
            } else {
                alert("卖请求失败" + result.message);
            }
        }, form);
    }
}

function determine(type) {

    var form = document.forms[type + 'Form'];
    if (type === 'buy' || type === 'sell') {
    } else {
        return;
    }

    var amount =  form.amount.value;
    if (amount == 0 || amount == '') {
        amount = 0;
    }

    var balance = (form.price.value * amount).toFixed(8);
    $('#' + type + 'Balance').text(balance);

    if (type === 'sell') {
        amount = balance;
    }

    var fee = (amount * form.tradingFeePercent.value / 100).toFixed(8);
    $('#' + type + 'Fee').text(fee);

    $('#' + type + 'TotalBalance').text((amount - fee).toFixed(8));
console.log(' -balance- :' + balance);
    return balance;
}
/*
* 实时当前买卖挂单
* */
function getRealTimeOrders(type) {
    tradeApi.getRealTimeOrders(function (result) {
        console.log('#########################getRealTimeOrders');
        console.log(JSON.stringify(result));

        if (result.status == 0) {

        }
    }, {coinPair: selectedCoinPair,orderType: type,pageNo: 1});
}
/*
* 交易历史记录
* */
function getMarketHistoryOrders() {
    tradeApi.getMarketHistoryOrders(function (result) {
        console.log('#########################getMarketHistoryOrders');
        console.log(JSON.stringify(result));

        if (result.status == 0) {

        }
    }, {coinPair: selectedCoinPair,pageNo: 1});
}
/*
* 取消订单
* */
function cancel(orderId) {
    if (confirm('确定要取消吗？')) {
        tradeApi.cancel(function (result) {

            console.log('#########################cancel');
            console.log(JSON.stringify(result));

            if (result.status == 0) {
                //此次取消成功的 订单设置成 取消成功
            }

            alert(result.message);
        }, {orderId: orderId});
    }
}
/*
* 我的当前挂单
* */
function getMyOrders() {
    tradeApi.getMyOrders(function (result) {
        console.log('#########################getMyOrders');
        console.log(JSON.stringify(result));

        if (result.status == 0) {
            var html = '', list = result.data.list;
            if (list.length === 0) {
                html = '<tr><td colspan="5" align="center">当前未挂单</td></tr>';
            } else {
                for (var i in list) {
                    html += '<tr>\n' +
                        '        <td>' +list[i].regDtm+ '</td>\n' +
                        '        <td>' +list[i].price+ '</td>\n' +
                        '        <td>' +list[i].amountRemaining+ '/' +list[i].amount+ '</td>\n' +
                        '        <td>' +list[i].orderType+ '</td>\n' +
                        '        <td><a href="javascript:cancel('+list[i].id+')">取消</a></td>\n' +
                        '    </tr>';
                }
            }
            $('#myOrders').html(html);
        }
    }, {coinPair: selectedCoinPair,pageNo: 1});
}
/*
* 我的交易历史记录
* */
function getMyHistoryOrders() {
    tradeApi.getMyHistoryOrders(function (result) {
        console.log('#########################getMyHistoryOrders');
        console.log(JSON.stringify(result));

        if (result.status == 0) {

        }
    }, {coinPair: selectedCoinPair,pageNo: 1});
}

function init() {
    getMyWalletsTradeCoin(selectedCoinPair);
    getRealTimeOrders('SELL');
    getRealTimeOrders('BUY');
    getMyOrders();
    getMarketHistoryOrders();
    getMyHistoryOrders();
}

$(function() {
    $("#buyPrice").keyup(function() {determine("buy")});
    $("#buyAmount").keyup(function() {determine("buy")});
    $("#sellPrice").keyup(function() {determine("sell")});
    $("#sellAmount").keyup(function() {determine("sell")});
    init();
});
