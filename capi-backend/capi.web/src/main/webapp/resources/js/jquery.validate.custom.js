/*
 * Custom error display method of jquery validate to suit for the use of bootstrap
 */
(function(){	
	if ($.validator != null){
		
		// set default appearance and behavior of the error handling
		$.validator.setDefaults({
			highlight: function(element, errorClass, validClass) {
				if ($(element).attr('type') == 'hidden') {
					var $form = $(element).closest('form');
					var $target = $form.find('.' + $(element).attr('name') + '-highlight');
					if ($target.length > 0) {
						$target.addClass('has-error');
					}
					return;
				}
				if ($(element).parent().parent().hasClass('btn-group')){
						$(element).parent().parent().addClass('has-error');
				}
				else if ($(element).parent().hasClass('input-group')){
					$(element).parent().addClass('has-error');
				}
				else if ($(element).parents(".radio-group").length > 0){
					$(element).parents(".radio-group:first").addClass('has-error');
				}
				else{
					$(element).parent().addClass('has-error');
				}
			 },
			 unhighlight: function(element, errorClass, validClass) {
				if ($(element).attr('type') == 'hidden') {
					var $form = $(element).closest('form');
					var $target = $form.find('.' + $(element).attr('name') + '-highlight');
					if ($target.length > 0) {
						$target.removeClass('has-error');
					}
					return;
				}
				 if ($(element).parent().parent().hasClass('btn-group')){
					$(element).parent().parent().removeClass('has-error');
					$(element).parent().parent().parent().find('.help-block').remove();
				}
				else if ($(element).parent().hasClass('input-group')){
					$(element).parent().removeClass('has-error');
					$(element).parent().parent().find('.help-block').remove();
				}
				else if ($(element).parents(".radio-group").length > 0){
					$(element).parents(".radio-group:first").removeClass('has-error');
					$(element).parents(".radio-group:first").find('.help-block').remove();
				}
				else{
					$(element).parent().removeClass('has-error');
					$(element).parent().find('.help-block').remove();
				}
			 },
			 errorPlacement: function(error, element) {
				 if ($(element).attr('type') == 'hidden') {
					var $form = $(element).closest('form');
					var $target = $form.find('.' + $(element).attr('name') + '-error-placement');
					if ($target.length > 0) {
						$target.append(error);
					}
					return;
			    }
				 
				 var div = $("<div class='help-block'></div>").append(error);
				if ($(element).parent().parent().hasClass('btn-group')){					
					$(element).parent().parent().after(div);
				}
				else if ($(element).parent().hasClass('input-group')){
					$(element).parent().after(div);
				}
				else if ($(element).parents(".radio-group").length > 0){
					$(element).parents(".radio-group:first").append(div);
				}
				else{
					$(element).parent().append(div);
				}
			 },
			 showErrors: function(errorMap, errorList) {	
				this.defaultShowErrors();
			}
		});
		
		
		// required based on the condition
		$.validator.addMethod('conditionRequired', function (value, element, param) {
		    if (param != null && param) {
		        return $.validator.methods.required.call(this, value, element, true);
		    }
		    return true;
		}, 'The field cannot be empty.');

		//required based on the condition
		$.validator.addMethod('conditionRules', function (value, element, param) {
		    if (param != null) {
		    	var self = this;
		    	return _.reduce(param, function (memo, ele){
		    		if (!memo) return false;
		    		if (ele.condition()){
		    			for (var key in ele){
		    				if (key == "condition") continue;
		    				var result = $.validator.methods[key].call(self, value, element, ele[key]);
		        			if (!result){
		        				var rule = { method: key, parameters: ele[key]};
		        				self.formatAndAdd( element, rule );
		        				if (self.settings.messages[element.name] == null){
		        					self.settings.messages[element.name]={};
		        					self.settings.messages[element.name]['conditionRules'] = $.validator.messages[key];
		        				}
		        				else{
		            				self.settings.messages[element.name].conditionRules = self.settings.messages[element.name][key] || $.validator.messages[key];
		        				}
		        				return false;
		        			}
		    			}
		    		}
					return memo;
		    	}, true);
		    }
		    return true;
		}, '');
		
		$.validator.addMethod('greaterThan', function (value, element, param){
			return parseFloat(value) > 0;
		},'')

		// validate the validness of HKID
		$.validator.addMethod('checkHKID', function(str, element, param){
				if (param != true) return true;	
			
		   		var strValidChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

		        // basic check length
		        if (str.length < 8)
		            return false;
		     
		        // handling bracket
		        if (str.charAt(str.length-3) == '(' && str.charAt(str.length-1) == ')')
		            str = str.substring(0, str.length - 3) + str.charAt(str.length -2);

		        // convert to upper case
		        str = str.toUpperCase();

		        // regular expression to check pattern and split
		        var hkidPat = /^([A-Z]{1,2})([0-9]{6})([A0-9])$/;
		        var matchArray = str.match(hkidPat);

		        // not match, return false
		        if (matchArray == null)
		            return false;

		        // the character part, numeric part and check digit part
		        var charPart = matchArray[1];
		        var numPart = matchArray[2];
		        var checkDigit = matchArray[3];

		        // calculate the checksum for character part
		        var checkSum = 0;
		        if (charPart.length == 2) {
		            checkSum += 9 * (10 + strValidChars.indexOf(charPart.charAt(0)));
		            checkSum += 8 * (10 + strValidChars.indexOf(charPart.charAt(1)));
		        } else {
		            checkSum += 9 * 36;
		            checkSum += 8 * (10 + strValidChars.indexOf(charPart));
		        }

		        // calculate the checksum for numeric part
		        for (var i = 0, j = 7; i < numPart.length; i++, j--)
		            checkSum += j * numPart.charAt(i);

		        // verify the check digit
		        var remaining = checkSum % 11;
		        var verify = remaining == 0 ? 0 : 11 - remaining;

		        return verify == checkDigit || (verify == 10 && checkDigit == 'A');
		});

		// validate the end date, must be on or after start date
		$.validator.addMethod('checkEndDate', function(value, element, param){
			var startDate = param;
			if( (startDate.val().length != 0) && (value.length != 0) ) {
				var valueArray = value.split('-');
				var startDateArray = startDate.val().split('-');
				if(new Date(valueArray[2], valueArray[1] - 1, valueArray[0]) 
					< new Date(startDateArray[2], startDateArray[1] - 1, startDateArray[0])) {
					return false;
				}
			}
			return true;
		}, 'The end date must be on or after the start date.');
		
		$.validator.addMethod('smaller_than_or_eq', function (value, element, param){
			if ($(element).val() == '' || $(param).val() == '') return true;
			return +$(element).val() <= +$(param).val();
		});
		
		$.validator.addMethod('not_smaller_than_or_eq', function (value, element, param){
			if ($(element).val() == '' || $(param).val() == '') return true;
			return +$(param).val() <= +$(element).val();
		});
		
		$.validator.addMethod('checkDateFormat', function(value, element) {
			var re = /^([0]?[1-9]|[1|2][0-9]|[3][0|1])[./-]([0]?[1-9]|[1][0-2])[./-]([0-9]{4})$/ ; 
			return re.test(value) && value != '';
		}, 'Please enter a valid date.');
		
	}
})()
