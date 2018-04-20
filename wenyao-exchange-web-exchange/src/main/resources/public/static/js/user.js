'use strict';



$.ajax({
    url: uri,
    type: "POST",
    dataType: 'json',
    contentType: "application/json; charset=UTF-8",
    data: JSON.stringify(params)
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