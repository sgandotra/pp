(function() {
        $(document).ready(function() {
            $("#select2Java").select2({
                placeholder : 'Click to select One or More Java Service(s)',
                dataType : 'json',
                quietMillis : 100,
                minimumInputLength : 3,
                ajax : {
                    url : '/java/servicepids',
                    data : function(term,page) {
                      return {
                          q : term
                      };
                    },
                    cache : true,
                    results : function(response,page) {
                        var select2Data = [],
                            index = 0;
                        $.each(response.data,function(indx,value) {
                            var processName = value.processName.split('/')[0] ;
                            if(value.isJava === true) {
                                var element = {
                                    id : index,
                                    text : processName
                                };
                                select2Data.push(element);
                                index++;
                            }
                        });
                        return { results : select2Data};
                    }

                },
                multiple: true,
                width: "300px",
                allowClear: true,
                dropdownCssClass: "bigdrop"

            });

            $("#select2Node").select2({
                placeholder : 'Click to Select One or More NodeJs Service(s)',
                dataType : 'json',
                quietMillis : 100,
                minimumInputLength : 3,
                ajax : {
                    url : 'http://stage2lp93.qa.paypal.com:3000/java/servicepids',
                    cache : true,
                    data : function(term,page) {
                        return {
                            q : term
                        };
                    },
                    results : function(response,page) {
                        var select2Data = [],
                            index = 0;
                        $.each(response.data,function(indx,value) {
                            var processName = value.processName.split('/')[0] ;
                            if(value.isJava === false && !value.processName.match(/atlas/)) {
                                var element = {
                                    id : index,
                                    text : processName
                                };
                                select2Data.push(element);
                                index++;
                            }
                        });
                        return { results : select2Data};
                    }

                },
                multiple: true,
                width: "300px",
                allowClear: true,
                dropdownCssClass: "bigdrop"

            });

            var _datePicker = $("#nmonSelector").datepicker({
                onRender: function(date) {
                    return date.valueOf() < new Date().valueOf() ? 'disabled' : '';
                }
                })
                .on('changeDate',function(event) {
                    if(event.date.valueOf() < new Date().valueOf()) {
                        $("#alert").css("display","block");
                    }
                    var temp = new Date(event.date.valueOf());
                    console.log(temp.getMonth()+1);
                    _datePicker.setValue( temp.getMonth()+1 + "-" + temp.getDate() +"-" +temp.getFullYear());
                    $("#nmonSelector input").val(temp.getMonth()+1 + "-" + temp.getDate() +"-" +temp.getFullYear()) ;
                    _datePicker.hide();
                }).data('datepicker');

            $("#pidchangehelp").popover({
                'html' : true
            });

            $("#javamonhelp").popover({
                'html' : true
            })

        });

    $.noty.defaults = {
        layout: 'bottomRight',
        theme: 'bootstrap',
        type: 'alert',
        text: '', // can be html or string
        dismissQueue: false, // If you want to use queue feature set this true
        template: '<div class="noty_message custom1"><span class="noty_text"></span><div class="noty_close"></div></div>',
        animation: {
            open: {height: 'toggle'},
            close: {height: 'toggle'},
            easing: 'swing',
            speed: 1000 // opening & closing animation speed
        },
        timeout: false, // delay for closing event. Set false for sticky notifications
        force: false, // adds notification to the beginning of queue when set to true
        modal: false,
        maxVisible: 5, // you can set max visible notification for dismissQueue true option,
        killer: true, // for close all notifications before show
        closeWith: ['click'], // ['click', 'button', 'hover']
        callback: {
            onShow: function() {},
            afterShow: function() {},
            onClose: function() {},
            afterClose: function() {}
        },
        buttons: false // an array of buttons
    };


})();


