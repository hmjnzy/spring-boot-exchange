'use strict';
function createWallet(coin) {
    walletApi.create(function(result) {
        if (result.status == 0) {
            alert('地址生成成功');
            location.href = 'depositManage?coin=' + coin;
        } else {
            alert('地址生成失败');
        }
    }, {coinName: coin});
}

function loadDepositTransactions(pageNo) {
    transactionApi.getTransactions(function(result) {
        var rows;
        if (result.status == 0) {
            result.data.list.forEach(function(r) {
                rows += '<tr>' +
                    '<td>' +r.status+ '</td>' +
                    '<td>' +r.address+ '</td>' +
                    '<td>' +r.amount+ '</td>' +
                    '<td>' +r.confirmation+ '</td>' +
                    '<td>' +r.regDt+ '</td>' +
                    '<td>' +(r.status === 'PENDING' ? '-' : r.completeDtm)+ '</td>' +
                    '<td>' +r.txId+ '</td>' +
                    '</tr>';//" / " + r.coin.depositAllowConfirmation+
            });
        }
        if (rows === undefined) {
            rows = '<tr><td colspan="7">无入款记录</td></tr>';
        }
        $('#depositTransactionBodyId').html(rows);
    }, {category: 'receive', coinName: selectedCoin, pageNo: pageNo, pageSize: 20});
}

function init() {
    loadDepositTransactions(1);
}

init();