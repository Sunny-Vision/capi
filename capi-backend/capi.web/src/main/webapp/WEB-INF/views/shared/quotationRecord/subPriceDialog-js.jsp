<%
/**
 * Display sub-price popup
 * 
 * Controller permission required:
 * /shared/General/getSubPriceFieldByType
 * /shared/General/getAllEnabledFormula
 * /shared/General/getSubPriceType
 * /shared/General/querySubPriceTypeSelect2
 */
%>
<script>
(function($, undefined){
	var _ori_Index = null;
	var Plugin = function(element, options) {
		this._process_options(options);
		
		this.$element = $(element);
		this.$popupModal = null;
		this.$subPriceTypeId = null;
		this.$table = null;
		this.fields = null;
		this.subPriceTypeDataModel = null;
		this.discountFormulas = null;
		this.$dialogTemplateContainer = this.$element.closest('.quotation-record-form').find('.sub-price-dialog-container');
		this.lastCalculationError = null;
		this.disabled = false;
		this.disabled2 = false;
		
		this.$element.click($.proxy(this._showPopup, this));
	};
	Plugin.prototype = {
		constructor : Plugin,
		_process_options : function(opts) {
			this._o = $.extend({}, this._o, opts);
			var o = this.o = $.extend({}, this._o);
		},
		_showPopup: function(e) {
			e.preventDefault();
			var plugin = this;
			this.disabled = this.$element.prop('disabled') || this.$element.attr('disabled') == 'disabled';
			
			if (this.$popupModal == null) {
				var $html = $(this.$dialogTemplateContainer.html());
				$(document.body).append($html);
				this.$popupModal = $html.modal('hide');

				this._initPopup();
				this.$popupModal.find('.databody tr').each(function() {
					plugin._initRow.call(plugin, $(this));
				});
				this.$popupModal.modal('show');
			} else {
				function cloneTable() {
					var $srcTable = plugin.$dialogTemplateContainer.find('.modal-dialog .datatable tbody');
					var $descTable = plugin.$popupModal.find('.modal-dialog .datatable tbody');
					$descTable.html($srcTable.html());
					if (!this.disabled && !this.disabled2) {
						$('.btn-delete', this.$popupModal).show();
					}
					plugin._cloneFormValues($srcTable, $descTable);
					plugin.$popupModal.find('.databody tr').each(function() {
						$.proxy(plugin._initRow, plugin, $(this))();
					});
				}
				
				function clearTable() {
					var $descTable = plugin.$popupModal.find('.modal-dialog .datatable');
					$descTable.empty();
				}
				
				if (this.$dialogTemplateContainer.find('select[name="subPriceTypeId"]').length > 0) {
					this.$subPriceTypeId.html(this.$dialogTemplateContainer.find('select[name="subPriceTypeId"]').html());
					if (this.$subPriceTypeId.val() != null) {
						this.$subPriceTypeId.one('_onSubPriceTypeChanged', function() {
							cloneTable();
						});
						this.$subPriceTypeId.trigger('change');
					} else {
						this.$subPriceTypeId.trigger('change');
						clearTable();
					}
				} else {
					cloneTable();
				}
				
				this.$popupModal.modal('show');
			}
			
			if (plugin.disabled || plugin.disabled2) {
				$('[name="subPriceTypeId"]', this.$popupModal).prop('disabled', true);
				$('.btn-add', plugin.$popupModal).addClass('hide');
				$('.modal-confirm,.modal-clear', this.$popupModal).addClass('hide');
			} else {
				$('[name="subPriceTypeId"]', this.$popupModal).prop('disabled', false);
				$('.btn-add', plugin.$popupModal).removeClass('hide');
				$('.modal-confirm,.modal-clear', this.$popupModal).removeClass('hide');
			}
		},
		_initPopup: function() {
			var plugin = this;
			this.$popupModal.find('.modal-content').wrap('<form></form>');
			this.$popupModal.find('form').validate();
			
			$('.btn-add', this.$popupModal).hide();
			this.$subPriceTypeId = $('[name="subPriceTypeId"]', this.$popupModal);
			this.$table = $('.datatable', this.$popupModal);

			if (this.$subPriceTypeId.is('select')) {
				this.$subPriceTypeId.select2ajax({
					width: '100%'
				});
			}
			
			this._downloadFields(function() {
				if (plugin.$subPriceTypeId.val() != null) {
					$('.btn-add', plugin.$popupModal).show();
				}
				
				if (plugin.subPriceTypeDataModel.hideNPrice) {
					$('.nprice-column', plugin.$table).addClass('hide');
					$('.nprice-column input,.nprice-column select', plugin.$table).removeAttr('required');
				}
				if (plugin.subPriceTypeDataModel.hideSPrice) {
					$('.sprice-column', plugin.$table).addClass('hide');
					$('.sprice-column input,.sprice-column select', plugin.$table).removeAttr('required');
				}
				if (plugin.subPriceTypeDataModel.hideDiscount)
					$('.discount-column', plugin.$table).addClass('hide');
			});
			
			$.get(this.o.getAllEnabledFormulaUrl, function(data) {
				plugin.discountFormulas = data;
				if (plugin.$subPriceTypeId.is('select')) {
					plugin.$subPriceTypeId.change($.proxy(plugin._onSubPriceTypeChange, plugin));
				}
			});
			
			this.$popupModal.on('click', '.btn-add', $.proxy(this._onAdd, this));
			
			this.$table.on('click', '.btn-delete', function() {
				var $btn = $(this);
				bootbox.confirm({
				     title:"Confirmation",
				     message: "<spring:message code='W00001' />",
				     callback: function(result) {
				    	var $tbody = plugin.$table.find('.databody').first();
				    	_ori_Index = $tbody.find('.datarow').length;
				         if (result) {
				        	 $btn.closest('tr').remove();
				         }
				     }
				});
			});
			
			this.$popupModal.on('click', '.modal-confirm', function() {
				if (plugin.$popupModal.find('form').valid())
					$.proxy(plugin._callback, plugin)();
			});
			
			this.$popupModal.on('click', '.modal-clear', function() {
				bootbox.confirm({
					title:"Confirmation",
					message: "<spring:message code='W00014' />",
					callback: function(result) {
						if (result) {
							$.proxy(function() {
								this.clear();
								this._callback();
							}, plugin)();
				    	 }
				     }
				});
			});
			
			if (this.disabled || this.disabled2) {
				$('.btn-delete', this.$popupModal).addClass('hide');
			}
			
			plugin.$table.on('change', '[name$=".columnValue"],[name$=".nPrice"],[name$=".sPrice"],[name$=".discount"]', function() {
				if ($(this).prop('type') == 'checkbox') {
					$(this).attr('checked', $(this).prop('checked'));
				} else {
					$(this).attr('value', $(this).val());
				}
			});
		},
		_initRow: function($row) {
			if (this.disabled || this.disabled2) return;
			Datepicker($('.date-picker', $row));
			$(".time-picker", $row).timepicker({
				showInputs: false,
				showMeridian: false,
				defaultTime: false,
				minuteStep: 1
	        });
			$('.btn-sub-price-show-discount-calculator', $row).discountCalculator({
				paramCallback: function(dataModel) {
					dataModel.nPrice = $('[name$=".nPrice"]', $row).val();
					dataModel.sPrice = $('[name$=".sPrice"]', $row).val();
					dataModel.discount = $('[name$=".discount"]', $row).val();
				},
				resultCallback: function(dataModel) {
					$('[name$=".nPrice"]', $row).val(dataModel.nPrice).trigger('change');
					$('[name$=".sPrice"]', $row).val(dataModel.sPrice).trigger('change');
					$('[name$=".discount"]', $row).val(dataModel.discount).trigger('change');
					if (dataModel.discount != ''){
						$('[name$=".nPrice"]', $row).prop('readonly', true);
						$('[name$=".sPrice"]', $row).prop('readonly', true);
					} else {
						$('[name$=".nPrice"]', $row).prop('readonly', false);
						$('[name$=".sPrice"]', $row).prop('readonly', false);
					}
				},
				hideRemark: true
			});
			if ($('[name$=".discount"]', $row).val() != ''){
				$('[name$=".nPrice"]', $row).prop('readonly', true);
				$('[name$=".sPrice"]', $row).prop('readonly', true);
			} else {
				$('[name$=".nPrice"]', $row).prop('readonly', false);
				$('[name$=".sPrice"]', $row).prop('readonly', false);
			}
		},
		_onSubPriceTypeChange: function() {
			var plugin = this;
			var subPriceTypeId = this.$subPriceTypeId.val();
			
			if (subPriceTypeId == null) {
				$('.btn-add', this.$popupModal).hide();
				return;
			}
			$('.btn-add', this.$popupModal).show();

			this.$table.empty();
			
			this._downloadFields(function(data) {
				var tableHeaderTemplate = Handlebars.compile($('.tableHeaderTemplate', plugin.$popupModal).html());
				plugin.$table.append(tableHeaderTemplate({ fields : plugin.fields, subPriceTypeDataModel: plugin.subPriceTypeDataModel }));
				plugin.$subPriceTypeId.trigger('_onSubPriceTypeChanged');
			});
		},
		_onAdd: function() {
			var tableRowTemplate = Handlebars.compile($('.tableRowTemplate', this.$popupModal).html());
			var $tbody = this.$table.find('.databody').first();
			var index = $tbody.find('.datarow').length;
			if(_ori_Index != null)
			{
				index = _ori_Index +1;
				_ori_Index  = _ori_Index +1;
			}
			
			var $row = $(tableRowTemplate({ index: index, fields: this.fields, subPriceTypeId: this.$subPriceTypeId.val(), subPriceTypeDataModel: this.subPriceTypeDataModel }));
			$tbody.append($row);
			this._initRow($row);
		},
		_downloadFields: function(callback) {
			var plugin = this;
			var subPriceTypeId = this.$subPriceTypeId.val();
			if (subPriceTypeId == null) return;
			$.get(this.o.getSubPriceFieldByTypeUrl + '?id=' + subPriceTypeId, function(data) {
				plugin.fields = data;
				
				$.get(plugin.o.getSubPriceTypeUrl + "?id=" + subPriceTypeId, function(subPriceTypeDataModel) {
					plugin.subPriceTypeDataModel = subPriceTypeDataModel;
					
					if (callback != undefined)
						callback(data);
				});
			});
		},
		_cloneFormValues: function($source, $dest) {
			/*
			var $destInputs = $dest.find('input');
			$source.find('input').each(function(index) {
				if ($($destInputs[index]).prop('type') == 'checkbox') {
					$($destInputs[index]).prop('checked', $(this).prop('checked'));
					return true;
				}
				$($destInputs[index]).val($(this).val());
			});
			*/
		},
		_calculatePriceBySubPrice: function(callback) {
			var plugin = this;
			this.lastCalculationError = null;
			var subPriceTypeId = this.$subPriceTypeId.val();
			if (subPriceTypeId == null) {
				callback(null);
				return;
			}
			$.get(this.o.getSubPriceTypeUrl + "?id=" + subPriceTypeId, function(data) {
				if (data.groupByField == null)
					$.proxy(plugin._calculateNormal, plugin, data, callback)();
				else
					$.proxy(plugin._calculateGroupBy, plugin, data, callback)();
			});
		},
		_getColumnValue: function(tableCell) {
			var $field = $('[name$=".columnValue"]', tableCell);
			if ($field.is('input:checkbox')) {
				if ($field.prop('checked'))
					return 1;
				else
					return 0;
			} else {
				return $field.val();
			}
		},
		_evalFormula: function(p, n, formula, variables) {
			try {
				for (var key in variables) {
					var val = variables[key];
					if (isNaN(val)) {
						val = (val == null ? 0: val);
						eval(key + "='" + val + "'");
					} else if (val !== undefined && $.trim(val).length > 0) {
						val = (val == null ? 0: val);
						eval(key + "=" + val);
					}
				}
				
				p = (p == null ? 0: p);
				n = (n == null ? 0: n);
				eval("p = " + p);
				eval("n = " + n);
				var result = eval(formula);
				if ((result + '').search(/^\-*\d+(\.\d+)*$/) != -1)
					return result;
				else
					return null;
			} catch (Exception){this.lastCalculationError = Exception;}
			return null;
		},
		_evalFormula2: function(n, formula, variables) {
			try {
				for (var key in variables) {
					var val = variables[key];
					if (isNaN(val)) {
						val = (val == null ? 0: val);
						eval(key + "='" + val + "'");
					} else if (val !== undefined && $.trim(val).length > 0) {
						val = (val == null ? 0: val);
						eval(key + "=" + val);
					}
				}
				
				n = (n == null ? 0: n);
				eval("n = " + n);
				if(formula.indexOf("p") >= 0)
					eval("p = " + 0);
				var result = eval(formula);
				if ((result + '').search(/^\-*\d+(\.\d+)*$/) != -1)
					return result;
				else
					return null;
			} catch (Exception){this.lastCalculationError = Exception;}
			return null;
		},
		
		_calculateNormal: function(formulaModel, callback) {
			var plugin = this;
			
			var maxNPrice, minNPrice, maxSPrice, minSPrice;
			if (plugin.subPriceTypeDataModel.useMaxNPrice ||
					plugin.subPriceTypeDataModel.useMinNPrice ||
					plugin.subPriceTypeDataModel.useMaxSPrice ||
					plugin.subPriceTypeDataModel.useMinSPrice) {
				$('.datarow', this.$table).each(function() {
					var nPrice = +$('[name$=".nPrice"]', this).val();
					var sPrice = +$('[name$=".sPrice"]', this).val();
					if (maxNPrice == null || maxNPrice < nPrice)
						maxNPrice = nPrice;
					if (minNPrice == null || minNPrice > nPrice)
						minNPrice = nPrice;
					if (maxSPrice == null || maxSPrice < sPrice)
						maxSPrice = sPrice;
					if (minSPrice == null || minSPrice > sPrice)
						minSPrice = sPrice;
				});
			}
			
			var numOfRow = $('.datarow', this.$table).length;
			if (numOfRow == 0) {
				this.lastCalculationError = 'empty_input';
				callback(null);
				return;
			}
			
			var nPriceNumeratorSum = 0;
			var nPriceDenominatorSum = 0;
			var sPriceNumeratorSum = 0;
			var sPriceDenominatorSum = 0;
			var hasError = false;
			$('.datarow', this.$table).each(function() {
				var variables = {};
				$('.field-column', this).each(function() {
					var variableName = $('[name$=".variableName"]', this).val();
					var columnValue = plugin._getColumnValue(this);
					variables[variableName] = columnValue;
				});
				
				if (!plugin.subPriceTypeDataModel.useMaxNPrice && !plugin.subPriceTypeDataModel.useMinNPrice) {
					var nPrice = $('[name$=".nPrice"]', this).val();
					
					if (!plugin.subPriceTypeDataModel.hideNPrice)
						var temp = plugin._evalFormula(nPrice, numOfRow, formulaModel.numeratorFormula, variables);
					else
						var temp = plugin._evalFormula2(numOfRow, formulaModel.numeratorFormula, variables);
					if (temp == null) {
						hasError = true;
						return false;
					} else {
						nPriceNumeratorSum += temp;
					}
					
					if (!plugin.subPriceTypeDataModel.hideNPrice)
						var temp = plugin._evalFormula(nPrice, numOfRow, formulaModel.denominatorFormula, variables);
					else
						var temp = plugin._evalFormula2(numOfRow, formulaModel.denominatorFormula, variables);
					if (temp == null) {
						hasError = true;
						return false;
					} else {
						nPriceDenominatorSum += temp;
					}
				}
				
				if (!plugin.subPriceTypeDataModel.useMaxSPrice && !plugin.subPriceTypeDataModel.useMinSPrice) {
					var sPrice = $('[name$=".sPrice"]', this).val();
					
					if (!plugin.subPriceTypeDataModel.hideSPrice)
						var temp = plugin._evalFormula(sPrice, numOfRow, formulaModel.numeratorFormula, variables);
					else
						var temp = plugin._evalFormula2(numOfRow, formulaModel.numeratorFormula, variables);
					if (temp == null) {
						hasError = true;
						return false;
					} else {
						sPriceNumeratorSum += temp;
					}
					
					if (!plugin.subPriceTypeDataModel.hideSPrice)
						var temp = plugin._evalFormula(sPrice, numOfRow, formulaModel.denominatorFormula, variables);
					else
						var temp = plugin._evalFormula2(numOfRow, formulaModel.denominatorFormula, variables);
					if (temp == null) {
						hasError = true;
						return false;
					} else {
						sPriceDenominatorSum += temp;
					}
				}
			});
			
			if (hasError) {
				callback(null);
				return;
			}
			
			try {
				var result = {nPrice:null,sPrice:null};
				if (!plugin.subPriceTypeDataModel.useMaxNPrice && !plugin.subPriceTypeDataModel.useMinNPrice) {
					result.nPrice = Math.round(nPriceNumeratorSum / nPriceDenominatorSum * 10000) / 10000;
				} else {
					if (plugin.subPriceTypeDataModel.useMaxNPrice)
						result.nPrice = maxNPrice;
					else if (plugin.subPriceTypeDataModel.useMinNPrice)
						result.nPrice = minNPrice;
				}
				if (!plugin.subPriceTypeDataModel.useMaxSPrice && !plugin.subPriceTypeDataModel.useMinSPrice) {
					result.sPrice = Math.round(sPriceNumeratorSum / sPriceDenominatorSum * 10000) / 10000;
				} else {
					if (plugin.subPriceTypeDataModel.useMaxSPrice)
						result.sPrice = maxSPrice;
					else if (plugin.subPriceTypeDataModel.useMinSPrice)
						result.sPrice = minSPrice;
				}
				if (isNaN(result.nPrice) || isNaN(result.sPrice)) {
					callback(null);
					return;
				}
				
				callback(result);
				return;
			} catch (Exception) {this.lastCalculationError = Exception;}
			callback(null);
		},
		
		_calculateGroupBy: function(formulaModel, callback) {
			var plugin = this;
			var numOfRow = $('.datarow', this.$table).length;
			if (numOfRow == 0) {
				this.lastCalculationError = 'empty_input';
				callback(null);
				return;
			}
			var groupByField = formulaModel.groupByField;
			var dividedByField = formulaModel.dividedByField;
			
			var groupMapping = {};
			$('.datarow', this.$table).each(function() {
				var $row = $(this);
				$('.field-column', this).each(function() {
					var variableName = $('[name$=".variableName"]', this).val();
					var columnValue = plugin._getColumnValue(this);
					
					if (groupByField == variableName) {
						if (groupMapping[columnValue] == undefined) {
							groupMapping[columnValue] = [];
						}
						groupMapping[columnValue].push($row);
					}
				});
			});
			
			var groupResults = {};
			for (var groupColumnValue in groupMapping) {
				
				var maxNPrice, minNPrice, maxSPrice, minSPrice;
				if (plugin.subPriceTypeDataModel.useMaxNPrice ||
						plugin.subPriceTypeDataModel.useMinNPrice ||
						plugin.subPriceTypeDataModel.useMaxSPrice ||
						plugin.subPriceTypeDataModel.useMinSPrice) {
					$(groupMapping[groupColumnValue]).each(function() {
						var nPrice = +$('[name$=".nPrice"]', this).val();
						var sPrice = +$('[name$=".sPrice"]', this).val();
						if (maxNPrice == null || maxNPrice < nPrice)
							maxNPrice = nPrice;
						if (minNPrice == null || minNPrice > nPrice)
							minNPrice = nPrice;
						if (maxSPrice == null || maxSPrice < sPrice)
							maxSPrice = sPrice;
						if (minSPrice == null || minSPrice > sPrice)
							minSPrice = sPrice;
					});
				}
				
				var numOfRow = groupMapping[groupColumnValue].length;
				var nPriceNumeratorSum = 0;
				var nPriceDenominatorSum = 0;
				var sPriceNumeratorSum = 0;
				var sPriceDenominatorSum = 0;
				var divideColumnValue = null;
				var hasError = false;
				$(groupMapping[groupColumnValue]).each(function() {
					var variables = {};
					$('.field-column', this).each(function() {
						var variableName = $('[name$=".variableName"]', this).val();
						var columnValue = plugin._getColumnValue(this);
						variables[variableName] = columnValue;
						
						if (dividedByField == variableName) {
							if (divideColumnValue == null) {
								divideColumnValue = columnValue;
							} else {
								if (columnValue != divideColumnValue) {
									plugin.lastCalculationError = "Group by fail. Different divide value found";
									hasError = true;
									return false;
								}
							}
						}
					});
					if (hasError) {
						return false;
					}
					
					if (!plugin.subPriceTypeDataModel.useMaxNPrice && !plugin.subPriceTypeDataModel.useMinNPrice) {
						var nPrice = $('[name$=".nPrice"]', this).val();
						
						if (!plugin.subPriceTypeDataModel.hideNPrice)
							var temp = plugin._evalFormula(nPrice, numOfRow, formulaModel.numeratorFormula, variables);
						else
							var temp = plugin._evalFormula2(numOfRow, formulaModel.numeratorFormula, variables);
						if (temp == null) {
							hasError = true;
							return false;
						} else {
							nPriceNumeratorSum += temp;
						}
						
						if (!plugin.subPriceTypeDataModel.hideNPrice)
							var temp = plugin._evalFormula(nPrice, numOfRow, formulaModel.denominatorFormula, variables);
						else
							var temp = plugin._evalFormula2(numOfRow, formulaModel.denominatorFormula, variables);
						if (temp == null) {
							hasError = true;
							return false;
						} else {
							nPriceDenominatorSum += temp;
						}
					}
					
					
					if (!plugin.subPriceTypeDataModel.useMaxSPrice && !plugin.subPriceTypeDataModel.useMinSPrice) {
						var sPrice = $('[name$=".sPrice"]', this).val();
						
						if (!plugin.subPriceTypeDataModel.hideSPrice)
							var temp = plugin._evalFormula(sPrice, numOfRow, formulaModel.numeratorFormula, variables);
						else
							var temp = plugin._evalFormula2(numOfRow, formulaModel.numeratorFormula, variables);
						if (temp == null) {
							hasError = true;
							return false;
						} else {
							sPriceNumeratorSum += temp;
						}
						
						if (!plugin.subPriceTypeDataModel.hideSPrice)
							var temp = plugin._evalFormula(sPrice, numOfRow, formulaModel.denominatorFormula, variables);
						else
							var temp = plugin._evalFormula2(numOfRow, formulaModel.denominatorFormula, variables);
						if (temp == null) {
							hasError = true;
							return false;
						} else {
							sPriceDenominatorSum += temp;
						}
					}
				});
				
				if (hasError) {
					callback(null);
					return;
				}
				
				try {
					var result = {nPrice:null,sPrice:null,divideColumnValue:divideColumnValue};
					if (!plugin.subPriceTypeDataModel.useMaxNPrice && !plugin.subPriceTypeDataModel.useMinNPrice) {
						result.nPrice = Math.round(nPriceNumeratorSum / nPriceDenominatorSum * 10000) / 10000;
					} else {
						if (plugin.subPriceTypeDataModel.useMaxNPrice)
							result.nPrice = maxNPrice;
						else if (plugin.subPriceTypeDataModel.useMinNPrice)
							result.nPrice = minNPrice;
					}
					if (!plugin.subPriceTypeDataModel.useMaxSPrice && !plugin.subPriceTypeDataModel.useMinSPrice) {
						result.sPrice = Math.round(sPriceNumeratorSum / sPriceDenominatorSum * 10000) / 10000;
					} else {
						if (plugin.subPriceTypeDataModel.useMaxSPrice)
							result.sPrice = maxSPrice;
						else if (plugin.subPriceTypeDataModel.useMinNPrice)
							result.sPrice = minSPrice;
					}
					groupResults[groupColumnValue] = result;
					continue;
				} catch (Exception) {this.lastCalculationError = Exception;}
				callback(null);
				return;
			}
			
			try {
				var sumN=0, sumS=0, sumDivide=0;
				for (groupResultKey in groupResults) {
					sumN += groupResults[groupResultKey].nPrice;
					sumS += groupResults[groupResultKey].sPrice;
					sumDivide += +groupResults[groupResultKey].divideColumnValue;
				}
				var result = {nPrice:null,sPrice:null};
				result.nPrice = Math.round(sumN / sumDivide * 10000) / 10000;
				result.sPrice = Math.round(sumS / sumDivide * 10000) / 10000;
				if (isNaN(result.nPrice) || isNaN(result.sPrice)) {
					callback(null);
					return;
				}
				
				callback(result);
				return;
			} catch (Exception) {this.lastCalculationError = Exception;}
			callback(null);
		},
		_callback: function() {
			var plugin = this;
		    this._calculatePriceBySubPrice(function(result) {
		    	if (plugin.lastCalculationError != null && plugin.lastCalculationError != 'empty_input') {
					bootbox.alert({
					     title:"Alert",
					     message: plugin.lastCalculationError
					});
					return;
				}
		    	
		    	if(plugin.subPriceTypeDataModel.hideSPrice) {
		    		if(result != undefined && (result.sPrice == null || result.sPrice == 0))
		    			result.sPrice = null;
		    	}
		    	if(plugin.subPriceTypeDataModel.hideNPrice) {
		    		if(result != undefined && (result.nPrice == null || result.nPrice == 0))
		    			result.nPrice = null;
		    	}
		    	plugin.$dialogTemplateContainer.find('.modal-dialog .datatable').empty().append(plugin.$popupModal.find('.modal-dialog .datatable').html());
		    	plugin._cloneFormValues(plugin.$popupModal.find('.modal-dialog .datatable'), plugin.$dialogTemplateContainer.find('.modal-dialog .datatable'));
		    	plugin.$dialogTemplateContainer.find('select[name="subPriceTypeId"]').html(plugin.$popupModal.find('select[name="subPriceTypeId"] option:selected').clone());
				
		    	plugin.$popupModal.modal('hide');
				$.proxy(plugin.o.resultCallback, plugin, result, plugin.lastCalculationError)();
		    });
		},
		clear: function() {
			if (this.$table == undefined) return;
			this.$table.find('.datarow').remove();
		},
		setDisabled2: function(value) {
			this.disabled2 = value;
		},
		clearSource: function() {
			var $srcTable = this.$dialogTemplateContainer.find('.modal-dialog .datatable tbody');
			$srcTable.empty();
		}
	};
	
	function opts_from_el(el, prefix) {
		// Derive options from element data-attrs
		var data = $(el).data(), out = {}, inkey, replace = new RegExp('^' + prefix.toLowerCase() + '([A-Z])');
		prefix = new RegExp('^' + prefix.toLowerCase());
		function re_lower(_, a) {
			return a.toLowerCase();
		}
		for ( var key in data)
			if (prefix.test(key)) {
				inkey = key.replace(replace, re_lower);
				out[inkey] = data[key];
			}
		return out;
	}
	
	$.fn.subPriceDialog = function(option) {
		var pluginDataName = 'subPriceDialog';
		var args = Array.apply(null, arguments);
		args.shift();
		var internal_return;
		this.each(function() {
			var $this = $(this),
				data = $this.data(pluginDataName),
				options = typeof option === 'object' && option;

			if (!data) {
				var elopts = opts_from_el(this, pluginDataName),
					opts = $.extend({}, defaults, elopts, options);
				$this.data(pluginDataName, (data = new Plugin(this, opts)));
			}
			if (typeof option === 'string' && typeof data[option] === 'function') {
				internal_return = data[option].apply(data, args);
				if (internal_return !== undefined)
					return false;
			}
		});
		if (internal_return !== undefined)
			return internal_return;
		else
			return this;
	}
	$.fn.subPriceDialog.Constructor = Plugin;

	var defaults = $.fn.subPriceDialog.defaults = {
		getSubPriceFieldByTypeUrl: "<c:url value='/shared/General/getSubPriceFieldByType'/>",
		getAllEnabledFormulaUrl: "<c:url value='/shared/General/getAllEnabledFormula'/>",
		getSubPriceTypeUrl: "<c:url value='/shared/General/getSubPriceType'/>",
		resultCallback: function(dataModel, lastCalculationError){}
	};
}(window.jQuery));
</script>