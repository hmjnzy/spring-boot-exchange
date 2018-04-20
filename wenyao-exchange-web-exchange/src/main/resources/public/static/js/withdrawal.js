'use strict';
function doWithdrawal(coin) {
    var form = formToObj(document.getElementById('withdrawalForm'));

    if (form.amount === '') {
        alert('请输入提现金额');
        return;
    }

    if (form.amount < parseFloat(form.minWithdrawalAmount)) {
        alert("需要申请 " + form.minWithdrawalAmount + " " + form.coinName + " 以上");
        return;
    }

    var availableBalance = parseFloat(form.availableBalance);
    if (availableBalance == 0) {
        alert('没有可提现余额');
        return;
    }

    if (form.amount  > parseFloat(form.availableBalance)) {
        alert("超过了 可提现金额 ( " + form.availableBalance + " " + form.coinName + " )");
        return;
    }

    if (parseFloat(form.myLimitAmount) < 0) {
        alert("超过了 1日 可提现 剩余金额 ( " + form.myLimitAmount + " " + form.coinName + " )");
        return;
    }

    if (form.amount > parseFloat(form.onedayAmount)) {
        alert("比 1日 可提现 ( " + form.onceAmount + " " + form.coinName + " ) 大");
        return;
    }

    if (form.amount > parseFloat(form.onceAmount)) {
        alert("比 每次 可提现 ( " + form.onceAmount + " " + form.coinName + " ) 大");
        return;
    }

    if (form.authCode == '') {
        alert("请输入 OTP Code");
        return;
    }

    if (form.address == '') {
        alert("请输入提现地址");
        return;
    }

    transactionApi.requestWithdrawal(function(result) {
        if (result.status == 0) {
            alert("提现成功");
            location.href = "/withdrawalManage?coin=" + selectedCoin;
        } else {
            alert(result.message);
        }
    }, form);
}

function loadWithdrawalTransactions(pageNo) {
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
                    '<td>' +(r.status === 'COMPLETED' ? r.txId : '-')+ '</td>' +
                    '</tr>';
            });
        }
        if (rows === undefined) {
            rows = '<tr><td colspan="7">无提现记录</td></tr>';
        }
        $('#withdrawalTransactionBodyId').html(rows);
    }, {category: 'send', coinName: selectedCoin, pageNo: pageNo, pageSize: 20});
}

function init() {
    loadWithdrawalTransactions(1);
}

init();