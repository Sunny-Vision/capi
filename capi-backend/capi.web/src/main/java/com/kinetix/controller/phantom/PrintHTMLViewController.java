package com.kinetix.controller.phantom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import capi.entity.Assignment;
import capi.model.masterMaintenance.SubPriceComparisonModel;
import capi.model.shared.quotationRecord.OutletViewModel;
import capi.model.shared.quotationRecord.PageViewModel;
import capi.service.assignmentManagement.AssignmentMaintenanceService;
import capi.service.assignmentManagement.QuotationRecordService;
import capi.service.masterMaintenance.SubPriceService;

@Controller("PrintHTMLViewController")
@RequestMapping("phantom/PrintHTMLView")
public class PrintHTMLViewController {


	@Autowired
	private QuotationRecordService quotationRecordService;
	@Autowired
	private SubPriceService subPriceService;
	@Autowired
	private AssignmentMaintenanceService assignmentService;
	
	@RequestMapping(value = "printQuotationRecord")
	public void printQuotationRecord(Model model, Integer id){
		PageViewModel viewModel = quotationRecordService.prepareViewModel(id, false, true);
		model.addAttribute("model", viewModel);
		model.addAttribute("printView", true);
		SubPriceComparisonModel record = subPriceService.prepareSubPriceComparisonModel(id, null);
		model.addAttribute("subPrice", record);
	}
	
	

	@RequestMapping(value = "printAssignmentHead")
	public void printAssignmentHead(Model model, Integer id){
		PageViewModel viewModel = new PageViewModel();
		Assignment assignment = assignmentService.getAssignmentById(id);
		OutletViewModel outletViewModel = quotationRecordService.prepareOutletViewModel(assignment.getOutlet());
		viewModel.setOutlet(outletViewModel);
		//PageViewModel viewModel = quotationRecordService.prepareViewModel(id, false, true);
		model.addAttribute("model", viewModel);
		model.addAttribute("printView", true);
//		SubPriceComparisonModel record = subPriceService.prepareSubPriceComparisonModel(id, null);
//		model.addAttribute("subPrice", record);
	}
	
}
