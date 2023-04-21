<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="headerCss" fragment="true" %>
<%@ attribute name="header" fragment="true" %>
<%@ attribute name="plainLayout"  %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>CAPI</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.5 -->
    <link rel="stylesheet" href="<c:url value='/resources/css/bootstrap.min.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/bootstrap-switch.min.css'/>" />
    <!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="<c:url value='/resources/css/skins/_all-skins.min.css'/>">
	<!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value='/resources/css/font-awesome.min.css'/>"/>
    <!-- Ionicons -->
    <link rel="stylesheet" href="<c:url value='/resources/css/ionicons.min.css'/>"/>
    
    <link rel="stylesheet" href="<c:url value='/resources/css/jquery-ui.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/resources/css/jquery-ui.structure.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/resources/css/jquery-ui.theme.css'/>"/>
    
	<jsp:invoke fragment="headerCss"/>
	
	<!-- Theme style -->
    <link rel="stylesheet" href="<c:url value='/resources/css/AdminLTE.css'/>">
    
    <link rel="stylesheet" href="<c:url value='/resources/css/common.css'/>"/>
	
	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="<c:url value='/resources/js/html5shiv.min.js'/>"></script>
        <script src="<c:url value='/resources/js/respond.min.js'/>"></script>
    <![endif]-->
    <style>
    	#notifcationMenu > li.notifcationContent,
    	#notifcationMenu > li.notifcationContent > div.slimScrollDiv,
    	#notifcationMenu > li.notifcationContent > div.slimScrollDiv > ul.menu {
    		height: 401px !important;
    		max-height: 401px !important;
    	}
    	.navbar-brand{
    		position: absolute;
		    left: 40%;
		    text-align: center;
		    margin:0 auto;
		    font-size: 30px !important;
		}  
    </style>
    <script src="<c:url value='/resources/js/jquery-1.11.1.min.js'/>"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="<c:url value='/resources/js/bootstrap.min.js'/>"></script>
    <script src="<c:url value='/resources/js/jquery.slimscroll.min.js'/>"></script>
    <script src="<c:url value='/resources/js/jquery.cookie.js'/>"></script>
    <script src="<c:url value='/resources/js/knockout-3.3.0.js'/>"></script>
    
    <script>
	    $(document).ajaxError(function(evt, jqXHR, setting, err){
	    	if (jqXHR.status == 401){
	    		// redirect to login page if unauthorized access
	    		window.location.href = "<c:url value='/Access/login'/>";
	    	}	    	
	    });

	    $.ajaxSetup({
	    	cache: false
	    });

    </script>
    <c:if test="${empty plainLayout or !plainLayout}" >
    	<script>
		    $(document).ready(function(){
		    	$("#notifyIcon").attr("disabled", "disabled");
		    	function notificationList(){
		    		this.notifications = ko.observableArray();
		    		this.count = ko.observable();
		    	}
		    	var notify = new notificationList();
		    	ko.applyBindings(notify, $("#notifcationMenu")[0]);
		    	$.get("<c:url value='/getNotification'/>",{}, function(data){
		    		notify.count(data.unReadCount);
		    		notify.notifications(data.notification);
		    		$("#notifyIcon").removeAttr("disabled");
		    		$("#notifcationLabel").addClass("label-warning").html(data.unReadCount);
		    	})
		    });
	   </script>
	</c:if>
    
	<jsp:invoke fragment="header"/>
</head>
<body class="hold-transition skin-blue sidebar-mini <c:if test="${cookie.menuStatus.value eq 'collapse'}" >sidebar-collapse</c:if>">
	<sec:authentication property="details" var="userDetail" />
	
	
	
	<div class="wrapper">
<c:if test="${empty plainLayout or !plainLayout}" >
      <header class="main-header">
        <!-- Logo -->
        <a href="<c:url value='/' />" class="logo">
          <!-- mini logo for sidebar mini 50x50 pixels -->
          <span class="logo-mini"><img src="<c:url value='/resources/images/logo.png'/>" style="border:0px; height:42px" /></span>
          <!-- logo for regular state and mobile devices -->
          <span class="logo-lg"><img src="<c:url value='/resources/images/logo.png'/>" style="border:0px; height:42px" /> CAPI</span>
        </a>
        <!-- Header Navbar: style can be found in header.less -->
        <nav class="navbar navbar-static-top" role="navigation">
          <!-- Sidebar toggle button-->
          
          <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="navbar-brand navbar-center" style="color: red;">限閱文件 <span style="color: red;font-weight:bold">RESTRICTED</span></a>
          <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
             
             <!-- Notifications: style can be found in dropdown.less -->
              <li class="dropdown notifications-menu">
                <a href="#" class="dropdown-toggle" id="notifyIcon" data-toggle="dropdown">
                  <i class="fa fa-bell-o"></i>
                  <span class="label" id="notifcationLabel">
                  	<img src="<c:url value='/resources/images/ui-anim_basic_16x16.gif' />" style="border:0px" />
                  </span>
                </a>
                <ul class="dropdown-menu" id="notifcationMenu">
                  <li class="header">You have <span data-bind="text:count"></span> notifications</li>
                  <li class="notifcationContent">
                    <!-- inner menu: contains the actual data -->
                    <ul class="menu" data-bind="foreach: notifications">                    	
                      <li>
                        <a data-bind="attr:{href: '<c:url value="/Notification/view" />?id='+notificationId }" >
                          <i class="fa fa-envelope-o text-aqua"></i> <span data-bind="text:subject"></span>
                          <span class="pull-right mailbox-read-time" data-bind="text:createdDate"></span>
                        </a>
                      </li>                      
                    </ul>
                  </li>
                  <li class="footer"><a href="<c:url value='/Notification/home'/>">View all</a></li>
                </ul>
                
              </li>
              <li class="dropdown">
              	<a href="<c:url value='/onlineHelp/${currentFunc}.html' />" target="_blank" >
                	<i class="fa fa-question" ></i>
                </a>
              </li>
              
              <!-- User Account: style can be found in dropdown.less -->
              <li class="dropdown user user-menu">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">                 
                  <span class="hidden-xs"><c:choose><c:when test="${empty userDetail.chineseName}">${userDetail.englishName}</c:when><c:otherwise>${userDetail.chineseName}</c:otherwise></c:choose></span>
                </a>
                <ul class="dropdown-menu">
                  <!-- User image -->
                  <li class="user-header">                   
                    <p>
                    <c:choose><c:when test="${empty userDetail.chineseName}">${userDetail.englishName}</c:when><c:otherwise>${userDetail.chineseName}</c:otherwise></c:choose> (${userDetail.staffCode})
                    </p>
                  </li>
                  
                  <!-- Menu Footer-->
                  <li class="user-footer">
                    <div class="pull-left">
                      <a href="<c:url value='/userAccountManagement/ChangePassword/edit'/>" class="btn btn-default btn-flat">Change Password</a>
                    </div>
                    <div class="pull-right">
                    	<c:url value="/j_spring_security_logout" var="logoutUrl" />
						<form action="${logoutUrl}" method="post">
							<input type="submit" class="btn btn-default btn-flat" value="Sign out"/>
						</form>                    
                    </div>
                  </li>
                </ul>
              </li>             
            </ul>
          </div>
        </nav>
      </header>
      <!-- Left side column. contains the logo and sidebar -->
      <aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
     
          <!-- /.search form -->
          <!-- sidebar menu: : style can be found in sidebar.less -->
          <ul class="sidebar-menu">
          
            <li <c:if test="${currentFunc eq 'RF1901'}">class="active"</c:if> > 
              <a href="<c:url value='/' />">
                <i class="fa fa-dashboard"></i> <span>Dashboard</span>
              </a>
            </li>
            
            <c:set var="masterMaintenance" value="'UF1112','UF1113','UF1207','UF1401','UF1402','UF1109','UF1116',
            										'UF1107','UF1108','UF1212','UF1114','UF1119','UF1102','UF1103','UF1105',
            										'UF1104','UF1115','UF1106','UF1408','UF1110'" />
            <sec:authorize access="hasAnyRole(${masterMaintenance})">
	             <li class="treeview <c:if test="${fn:contains(masterMaintenance, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-tree-conifer"></i><span>Master Maintenance</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            <sec:authorize access="hasRole('UF1112')">
						<li <c:if test="${currentFunc eq 'UF1112'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/BusinessParameterMaintenance/home'/>"><i class="fa fa-circle-o"></i>Business Parameter Maintenance</a></li>
					</sec:authorize>
		            <sec:authorize access="hasRole('UF1113')">
						<li <c:if test="${currentFunc eq 'UF1113'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/ClosingDateMaintenance/home'/>"><i class="fa fa-circle-o"></i>Closing Date Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1207')">
						<li <c:if test="${currentFunc eq 'UF1207'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/UnitMaintenance/home'/>"><i class="fa fa-circle-o"></i>Variety Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1401')">
		                <li <c:if test="${currentFunc eq 'UF1401'}">class="active"</c:if>><a href="<c:url value='/assignmentManagement/QuotationMaintenance/home'/>"><i class="fa fa-circle-o"></i>Quotation Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1402')">
		                <li <c:if test="${currentFunc eq 'UF1402'}">class="active"</c:if>><a href="<c:url value='/assignmentManagement/QuotationRecordMaintenance/home'/>"><i class="fa fa-circle-o"></i>Quotation Record Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1109')">
						<li <c:if test="${currentFunc eq 'UF1109'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/SubPriceMaintenance/home'/>"><i class="fa fa-circle-o"></i>Sub-Price Type Maintenance</a></li>
					</sec:authorize>
		            <sec:authorize access="hasRole('UF1116')">
						<li <c:if test="${currentFunc eq 'UF1116'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/SubPriceFieldMaintenance/home'/>"><i class="fa fa-circle-o"></i>Sub-Price Field Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1107')">
						<li <c:if test="${currentFunc eq 'UF1107'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/DiscountCalculatorMaintenance/home'/>"><i class="fa fa-circle-o"></i>Discount Calculator Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1108')">
						<li <c:if test="${currentFunc eq 'UF1108'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/DiscountFormulaMaintenance/home'/>"><i class="fa fa-circle-o"></i>Discount Formula Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1212')">
						<li <c:if test="${currentFunc eq 'UF1212'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/SurveyTypeMaintenance/home'/>"><i class="fa fa-circle-o"></i>Purpose Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1114')">
						<li <c:if test="${currentFunc eq 'UF1114'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/BatchMaintenance/home'/>"><i class="fa fa-circle-o"></i>Batch Code Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1119')">
						<li <c:if test="${currentFunc eq 'UF1119'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/PricingMonthMaintenance/home'/>"><i class="fa fa-circle-o"></i>Pricing Month Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1102')">
		                <li <c:if test="${currentFunc eq 'UF1102'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/DistrictMaintenance/home'/>"><i class="fa fa-circle-o"></i>District Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1103')">
						<li <c:if test="${currentFunc eq 'UF1103'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/TpuMaintenance/home'/>"><i class="fa fa-circle-o"></i>TPU Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1105')">
						<li <c:if test="${currentFunc eq 'UF1105'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/UomMaintenance/home'/>"><i class="fa fa-circle-o"></i>UOM Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1104')">
						<li <c:if test="${currentFunc eq 'UF1104'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/UOMCategoryMaintenance/home'/>"><i class="fa fa-circle-o"></i>UOM Category Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1115')">
						<li <c:if test="${currentFunc eq 'UF1115'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/UOMConversionMaintenance/home'/>"><i class="fa fa-circle-o"></i>UOM Conversion Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1106')">
						<li <c:if test="${currentFunc eq 'UF1106'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/PriceReasonMaintenance/home'/>"><i class="fa fa-circle-o"></i>Price Reason Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1408')">
						<li <c:if test="${currentFunc eq 'UF1408'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/QuotationLoadingMaintenance/home'/>"><i class="fa fa-circle-o"></i>Quotation Loading Maintenance</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('UF1110')">
						<li <c:if test="${currentFunc eq 'UF1110'}">class="active"</c:if>><a href="<c:url value='/masterMaintenance/ActivityCodeMaintenance/home'/>"><i class="fa fa-circle-o"></i>Activity Code Maintenance</a></li>
					</sec:authorize>				
					
		            </ul>
	             </li>
             </sec:authorize>
             
             <sec:authorize access="hasRole('UF1101')">
                <li <c:if test="${currentFunc eq 'UF1101'}">class="active"</c:if>>
	                <a href="<c:url value='/masterMaintenance/OutletMaintenance/home'/>">
	                	<i class="glyphicon glyphicon-home"></i><span>Outlet Maintenance</span>
	                </a>
                </li>
            </sec:authorize>
		            
            
            <c:set var="productMaintenance" value="'UF1208','UF1209'" />
            <sec:authorize access="hasAnyRole(${productMaintenance})">
	             <li class="treeview <c:if test="${fn:contains(productMaintenance, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-barcode"></i><span>Product Maintenance</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            <sec:authorize access="hasRole('UF1208')">
		                <li <c:if test="${currentFunc eq 'UF1208'}">class="active"</c:if>><a href="<c:url value='/productMaintenance/ProductGroupMaintenance/home'/>"><i class="fa fa-circle-o"></i>Product Type Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1209')">
		                <li <c:if test="${currentFunc eq 'UF1209'}">class="active"</c:if>><a href="<c:url value='/productMaintenance/ProductMaintenance/home'/>"><i class="fa fa-circle-o"></i>Product Maintenance</a></li>
		            </sec:authorize>
		            </ul>
	             </li>
             </sec:authorize>
             
            <c:set var="userAccountManagement" value="'UF1301','UF1117','UF1302','UF1304','UF1305','UF1306','UF1307'" />
            <sec:authorize access="hasAnyRole(${userAccountManagement})">
	             <li class="treeview <c:if test="${fn:contains(userAccountManagement, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-user"></i><span>User Account Management</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            <sec:authorize access="hasRole('UF1301')">
		                <li <c:if test="${currentFunc eq 'UF1301'}">class="active"</c:if>><a href="<c:url value='/userAccountManagement/StaffProfileMaintenance/home'/>"><i class="fa fa-circle-o"></i>Staff Profile Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1307')">
		                <li <c:if test="${currentFunc eq 'UF1307'}">class="active"</c:if>><a href="<c:url value='/userAccountManagement/PasswordPolicyMaintenance/home'/>"><i class="fa fa-circle-o"></i>Password Policy Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1306')">
		                <li <c:if test="${currentFunc eq 'UF1306'}">class="active"</c:if>><a href="<c:url value='/userAccountManagement/ChangePassword/edit'/>"><i class="fa fa-circle-o"></i>Change Password</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1302')">
		                <li <c:if test="${currentFunc eq 'UF1302'}">class="active"</c:if>><a href="<c:url value='/userAccountManagement/ActingFunction/home'/>"><i class="fa fa-circle-o"></i>Acting function</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1304')">
		                <li <c:if test="${currentFunc eq 'UF1304'}">class="active"</c:if>><a href="<c:url value='/userAccountManagement/RoleMaintenance/home'/>"><i class="fa fa-circle-o"></i>Role Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1117')">
		                <li <c:if test="${currentFunc eq 'UF1117'}">class="active"</c:if>><a href="<c:url value='/userAccountManagement/RankMaintenance/home'/>"><i class="fa fa-circle-o"></i>Rank Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1305')">
		                <li <c:if test="${currentFunc eq 'UF1305'}">class="active"</c:if>><a href="<c:url value='/userAccountManagement/FieldExperienceMaintenance/home'/>"><i class="fa fa-circle-o"></i>Field Officer Profile Maintenance</a></li>
		            </sec:authorize>
		            
		            </ul>
	             </li>
             </sec:authorize>
             
            <c:set var="assignmentManagement" value="'UF1406','UF2602','UF1410','UF1409'" />
            <sec:authorize access="hasAnyRole(${assignmentManagement})">
	             <li class="treeview <c:if test="${fn:contains(assignmentManagement, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-check"></i><span>Assignment Management (Field Supervisor)</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            
		            <sec:authorize access="hasRole('UF1406')">
		                <li <c:if test="${currentFunc eq 'UF1406'}">class="active"</c:if>><a href="<c:url value='/assignmentManagement/AssignmentApproval/home'/>"><i class="fa fa-circle-o"></i>Assignment Approval</a></li>
		            </sec:authorize>		            
		            <sec:authorize access="hasRole('UF2602')">
		                <li <c:if test="${currentFunc eq 'UF2602'}">class="active"</c:if>><a href="<c:url value='/assignmentManagement/NewRecruitmentApproval/home'/>"><i class="fa fa-circle-o"></i>New Recruitment Approval</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1410')">
		                <li <c:if test="${currentFunc eq 'UF1410'}">class="active"</c:if>><a href="<c:url value='/assignmentManagement/RUACaseApproval/home'/>"><i class="fa fa-circle-o"></i>RUA Case Approval</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1409')">
		                <li <c:if test="${currentFunc eq 'UF1409'}">class="active"</c:if>><a href="<c:url value='/assignmentManagement/RUASetting/home'/>"><i class="fa fa-circle-o"></i>RUA Setting</a></li>
		            </sec:authorize>
		            </ul>
	             </li>
             </sec:authorize>
             
             
             
              <c:set var="assignmentManagement" value="'UF1405','UF2601'" />
            <sec:authorize access="hasAnyRole(${assignmentManagement})">
	             <li class="treeview <c:if test="${fn:contains(assignmentManagement, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-pencil"></i><span>Assignment Management (Field Officer)</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            
		            <sec:authorize access="hasRole('UF1405')">
		                <li <c:if test="${currentFunc eq 'UF1405'}">class="active"</c:if>><a href="<c:url value='/assignmentManagement/AssignmentMaintenance/home'/>"><i class="fa fa-circle-o"></i>Assignment Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF2601')">
		                <li <c:if test="${currentFunc eq 'UF2601'}">class="active"</c:if>><a href="<c:url value='/assignmentManagement/NewRecruitmentMaintenance/home'/>"><i class="fa fa-circle-o"></i>New Recruitment Maintenance</a></li>
		            </sec:authorize>
		            </ul>
	             </li>
             </sec:authorize>
                          
             
            <c:set var="assignmentAllocation" value="'UF1501','UF1502','UF1503','UF1504','UF1505','UF1506','UF1507','UF1508','UF1303'" />
            <sec:authorize access="hasAnyRole(${assignmentAllocation})">
	             <li class="treeview <c:if test="${fn:contains(assignmentAllocation, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-map-marker"></i><span>Assignment Allocation &amp; Reallocation</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		             <sec:authorize access="hasRole('UF1303')">
		                <li <c:if test="${currentFunc eq 'UF1303'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/StaffCalendar/home?clearSession=1'/>"><i class="fa fa-circle-o"></i>Staff Calendar</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1501')">
		                <li <c:if test="${currentFunc eq 'UF1501'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/SurveyMonth/home'/>"><i class="fa fa-circle-o"></i>Survey Month</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1502')">
		                <li <c:if test="${currentFunc eq 'UF1502'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/AssignmentAllocation/home'/>"><i class="fa fa-circle-o"></i>Assignment Allocation</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1504')">
		                <li <c:if test="${currentFunc eq 'UF1504'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutApproval/home'/>"><i class="fa fa-circle-o"></i>Assignment Transfer-in/out Approval</a></li>
		            </sec:authorize>
		             <sec:authorize access="hasRole('UF1506')">
		                <li <c:if test="${currentFunc eq 'UF1506'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutRecommendation/home'/>"><i class="fa fa-circle-o"></i>Assignment Transfer-in/out Recommendation</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1503')">
		                <li <c:if test="${currentFunc eq 'UF1503'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/AssignmentTransferInOutMaintenance/home'/>"><i class="fa fa-circle-o"></i>Assignment Transfer-in/out Maintenance</a></li>
		            </sec:authorize>
		            
		            
		            <sec:authorize access="hasRole('UF1507')">
		                <li <c:if test="${currentFunc eq 'UF1507'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationApproval/home'/>"><i class="fa fa-circle-o"></i>Assignment Reallocation Approval</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1508')">
		                <li <c:if test="${currentFunc eq 'UF1508'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocationRecommendation/home'/>"><i class="fa fa-circle-o"></i>Assignment Reallocation Recommendation</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1505')">
		                <li <c:if test="${currentFunc eq 'UF1505'}">class="active"</c:if>><a href="<c:url value='/assignmentAllocationAndReallocation/AssignmentReallocation/home'/>"><i class="fa fa-circle-o"></i>Assignment Reallocation</a></li>
		            </sec:authorize>		           
		           
		            </ul>
	             </li>
             </sec:authorize>
                          
             
            <c:set var="itineraryPlanning" value="'UF1701','UF1702','UF1703','UF1704'" />
            <sec:authorize access="hasAnyRole(${itineraryPlanning})">
	             <li class="treeview <c:if test="${fn:contains(itineraryPlanning, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-th-list"></i><span>Itinerary Planning</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            <sec:authorize access="hasRole('UF1702')">
		                <li <c:if test="${currentFunc eq 'UF1702'}">class="active"</c:if>><a href="<c:url value='/itineraryPlanning/ItineraryPlanApproval/home'/>"><i class="fa fa-circle-o"></i>Itinerary Plan Approval</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1701')">
		                <li <c:if test="${currentFunc eq 'UF1701'}">class="active"</c:if>><a href="<c:url value='/itineraryPlanning/ItineraryPlan/home'/>"><i class="fa fa-circle-o"></i>Itinerary Plan Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1704')">
		                <li <c:if test="${currentFunc eq 'UF1704'}">class="active"</c:if>><a href="<c:url value='/itineraryPlanning/QCItineraryPlanApproval/home'/>"><i class="fa fa-circle-o"></i>Field Supervisor Itinerary Plan Approval</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1703')">
		                <li <c:if test="${currentFunc eq 'UF1703'}">class="active"</c:if>><a href="<c:url value='/itineraryPlanning/QCItineraryPlan/home'/>"><i class="fa fa-circle-o"></i>Field Supervisor Itinerary Plan Maintenance</a></li>
		            </sec:authorize>		            
		            </ul>
	             </li>
             </sec:authorize>
             
            <c:set var="timeLog" value="'UF1801','UF1802'" />
            <sec:authorize access="hasAnyRole(${timeLog})">
	             <li class="treeview <c:if test="${fn:contains(timeLog, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-time"></i><span>Time Log Management</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            <sec:authorize access="hasRole('UF1801')">
		                <li <c:if test="${currentFunc eq 'UF1801'}">class="active"</c:if>><a href="<c:url value='/timeLogManagement/TimeLogMaintenance/home'/>"><i class="fa fa-circle-o"></i>Time Log Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF1802')">
		                <li <c:if test="${currentFunc eq 'UF1802'}">class="active"</c:if>><a href="<c:url value='/timeLogManagement/ItineraryCheckingApproval/home'/>"><i class="fa fa-circle-o"></i>Reconciliation Checking Approval</a></li>
		            </sec:authorize>
		            </ul>
	             </li>
             </sec:authorize>
                    
             
            <c:set var="qualityControlManagement" value="'RF2001','RF2002','RF2003','RF2004','RF2005','RF2006','RF2008','RF2009','RF2013','RF2014'" />
            <sec:authorize access="hasAnyRole(${qualityControlManagement})">
	             <li class="treeview <c:if test="${fn:contains(qualityControlManagement, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-thumbs-up"></i><span>Quality Control Management</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            <sec:authorize access="hasRole('RF2004')">
		                <li <c:if test="${currentFunc eq 'RF2004'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/SpotCheckDateMaintenance/home'/>"><i class="fa fa-circle-o"></i>Spot Check Date Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('RF2005')">
		                <li <c:if test="${currentFunc eq 'RF2005'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/SpotCheckSetupMaintenance/home'/>"><i class="fa fa-circle-o"></i>Spot Check Setup Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('RF2001')">
		                <li <c:if test="${currentFunc eq 'RF2001'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/SpotCheckMaintenance/home'/>"><i class="fa fa-circle-o"></i>Spot Check Maintenance</a></li>
		            </sec:authorize>
		             <sec:authorize access="hasRole('RF2013')">
		                <li <c:if test="${currentFunc eq 'RF2013'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/SpotCheckApproval/home'/>"><i class="fa fa-circle-o"></i>Spot Check Approval</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('RF2006')">
		                <li <c:if test="${currentFunc eq 'RF2006'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/SupervisoryVisitPlanMaintenance/home'/>"><i class="fa fa-circle-o"></i>Spot Check / Supervisory Visit Plan Maintenance</a></li>
		            </sec:authorize>
		            
		            
		            <sec:authorize access="hasRole('RF2002')">
		                <li <c:if test="${currentFunc eq 'RF2002'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/SupervisoryVisitMaintenance/home'/>"><i class="fa fa-circle-o"></i>Supervisory Visit Maintenance</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('RF2008')">
		                <li <c:if test="${currentFunc eq 'RF2008'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/SupervisoryVisitApproval/home'/>"><i class="fa fa-circle-o"></i>Supervisory Visit Approval</a></li>
		            </sec:authorize>
		            
		            <sec:authorize access="hasRole('RF2014')">
		                <li <c:if test="${currentFunc eq 'RF2014'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/PostEnumerationCertaintyCaseMaintenance/home'/>"><i class="fa fa-circle-o"></i>Post-Enumeration Certainty Case Maintenance</a></li>
		            </sec:authorize>
		            
		            <sec:authorize access="hasRole('RF2009')">
		                <li <c:if test="${currentFunc eq 'RF2009'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/PostEnumerationCheckListMaintenance/home'/>"><i class="fa fa-circle-o"></i>Post-Enumeration Check List Maintenance</a></li>
		            </sec:authorize>
		            
		            <sec:authorize access="hasRole('RF2003')">
		                <li <c:if test="${currentFunc eq 'RF2003'}">class="active"</c:if>><a href="<c:url value='/qualityControlManagement/PostEnumerationCheckMaintenance/home'/>"><i class="fa fa-circle-o"></i>Post-Enumeration Check Maintenance</a></li>
		            </sec:authorize>
		            
		            </ul>
	             </li>
             </sec:authorize>
             
            <c:set var="dataConversion" value="'UF2101','UF2102'" />
            <sec:authorize access="hasAnyRole(${dataConversion})">
	             <li class="treeview <c:if test="${fn:contains(dataConversion, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-retweet"></i><span>Data Conversion</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            <sec:authorize access="hasRole('UF2101')">
		                <li <c:if test="${currentFunc eq 'UF2101'}">class="active"</c:if>><a href="<c:url value='/dataConversion/QuotationRecordDataConversion/home'/>"><i class="fa fa-circle-o"></i>Quotation Record Data Conversion</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF2102')">
		                <li <c:if test="${currentFunc eq 'UF2102'}">class="active"</c:if>><a href="<c:url value='/dataConversion/AllocateQuotationRecordDataConversion/home'/>"><i class="fa fa-circle-o"></i>Allocate Quotation Record Data Conversion</a></li>
		            </sec:authorize>		            
		            </ul>
	             </li>
             </sec:authorize>
             
             
             <sec:authorize access="hasRole('UF2103')">
                <li <c:if test="${currentFunc eq 'UF2103'}">class="active"</c:if>>
	             	<a href="<c:url value='/QuotationRecordVerificationApproval/home'/>">
                		<i class="glyphicon glyphicon-saved"></i><span>Quotation Record Verification Approval</span>
                	</a>
                </li>
            </sec:authorize>
            <sec:authorize access="hasRole('UF2201')">
	             <li <c:if test="${currentFunc eq 'UF2201'}">class="active"</c:if>>
	             	<a href="<c:url value='/QuotationRecordDataReview/home'/>">
		            	<i class="glyphicon glyphicon-eye-open"></i><span>Quotation Record Data Review</span> 
		            </a>
	             </li>
             </sec:authorize>
            
            <sec:authorize access="hasRole('UF1118')">
				<li <c:if test="${currentFunc eq 'UF1118'}">class="active"</c:if>>
					<a href="<c:url value='/masterMaintenance/PointToNoteMaintenance/home'/>">
						<i class="glyphicon glyphicon-file"></i><span>Point-To-Note Maintenance</span>
					</a>
				</li>
			</sec:authorize>
					
					
            <c:set var="dataImportExport" value="'UF2401','UF2402'" />
            <sec:authorize access="hasAnyRole(${dataImportExport})">
	             <li class="treeview <c:if test="${fn:contains(dataImportExport, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-transfer"></i><span>Data Import &amp; Export</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
		            <sec:authorize access="hasRole('UF2401')">
		                <li <c:if test="${currentFunc eq 'UF2401'}">class="active"</c:if>><a href="<c:url value="/dataImportExport/DataImport/home" />"><i class="fa fa-circle-o"></i>Data Import</a></li>
		            </sec:authorize>
		            <sec:authorize access="hasRole('UF2402')">
		                <li <c:if test="${currentFunc eq 'UF2402'}">class="active"</c:if>><a href="<c:url value="/dataImportExport/DataExport/home" />"><i class="fa fa-circle-o"></i>Data Export</a></li>
		            </sec:authorize>
		            </ul>
	             </li>
             </sec:authorize>
             
            <sec:authorize access="hasRole('UF2701')">
	             <li <c:if test="${currentFunc eq 'UF2701'}">class="active"</c:if>>
	             	<a href="<c:url value="/Notification/home" />">
		            	<i class="glyphicon glyphicon-envelope"></i><span>Notification</span>
		            </a>
	             </li>
             </sec:authorize>
             
             <c:set var="report" value="'UF9001','RF9002','RF9003','RF9004','RF9005','RF9006','RF9007','RF9008','RF9009','RF9010'
             								,'RF9011','RF9012','RF9013','RF9014','RF9015','RF9016','RF9017','RF9018','RF9019','RF9020'
             								,'RF9021','RF9022','RF9023','RF9024','RF9025','RF9026','RF9027','RF9028','RF9029','RF9030'
             								,'RF9031','RF9032','RF9033','RF9034','RF9035','RF9036','RF9037','RF9038','RF9039','RF9040'
             								,'RF9041','RF9042','RF9043','RF9044','RF9045','RF9046','RF9047','RF9048','RF9049','RF9050'
             								,'UF9051'" />
            <sec:authorize access="hasAnyRole(${report})">
	             <li class="treeview <c:if test="${fn:contains(report, currentFunc)}">active</c:if>">
	             	<a href="javascript:void(0)">
		            	<i class="glyphicon glyphicon-list-alt"></i><span>Report &amp; Audit Log</span> <i class="fa fa-angle-left pull-right"></i>
		            </a>
		            <ul class="treeview-menu">
			            <sec:authorize access="hasRole('UF9001')">
			                <li <c:if test="${currentFunc eq 'UF9001'}">class="active"</c:if>><a href="<c:url value="/report/AuditTrail/home" />"><i class="fa fa-circle-o"></i>Audit Trail</a></li>
			            </sec:authorize>
			            <sec:authorize access="hasRole('UF9051')">
			                <li <c:if test="${currentFunc eq 'UF9051'}">class="active"</c:if>><a href="<c:url value="/report/AccessLog/home" />"><i class="fa fa-circle-o"></i>Access Log (Log in/out)</a></li>
			            </sec:authorize>
		            	<sec:authorize access="hasRole('RF9002')">
			                <li <c:if test="${currentFunc eq 'RF9002'}">class="active"</c:if>><a href="<c:url value="/report/QuotationRecordProgress/home"/>"><i class="fa fa-circle-o"></i>Progress Report (by Stage & Variety)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9003')">
			                <li <c:if test="${currentFunc eq 'RF9003'}">class="active"</c:if>><a href="<c:url value="/report/IndoorStaffIndividualProgress/home"/>"><i class="fa fa-circle-o"></i>Indoor Officer Progress Report (Individual Indoor Officer by Sub-group & Stage)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9004')">
			                <li <c:if test="${currentFunc eq 'RF9004'}">class="active"</c:if>><a href="<c:url value="/report/IndoorStaffProgress/home"/>"><i class="fa fa-circle-o"></i>Indoor Officer Progress report (Overview by Officer)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9005')">
			                <li <c:if test="${currentFunc eq 'RF9005'}">class="active"</c:if>><a href="<c:url value="/report/OutrangedQuotationRecords/home"/>"><i class="fa fa-circle-o"></i>List of Outranged Quotation Records</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9006')">
			                <li <c:if test="${currentFunc eq 'RF9006'}">class="active"</c:if>><a href="<c:url value="/report/CrossCheck/home"/>"><i class="fa fa-circle-o"></i>Variety PR Cross-check Report</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9007')">
			                <li <c:if test="${currentFunc eq 'RF9007'}">class="active"</c:if>><a href="<c:url value="/report/ListOfQuotationRecords/home"/>"><i class="fa fa-circle-o"></i>List of Quotation Records with Specific Filterings for Micro-review</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9008')">
			                <li <c:if test="${currentFunc eq 'RF9008'}">class="active"</c:if>><a href="<c:url value="/report/NewRecruitmentsAndProductReplacements/home"/>"><i class="fa fa-circle-o"></i>List of Quotation Records with New Recruitments and Product Replacements</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9009')">
			                <li <c:if test="${currentFunc eq 'RF9009'}">class="active"</c:if>><a href="<c:url value="/report/ProductReview/home"/>"><i class="fa fa-circle-o"></i>Product Review Report</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9010')">
			                <li <c:if test="${currentFunc eq 'RF9010'}">class="active"</c:if>><a href="<c:url value="/report/AirportTicketPRListByCountryWithCPISeries/home"/>"><i class="fa fa-circle-o"></i>Airport Ticket PR List (by Country with CPI Series)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9011')">
			                <li <c:if test="${currentFunc eq 'RF9011'}">class="active"</c:if>><a href="<c:url value="/report/FRAdjustment/home"/>"><i class="fa fa-circle-o"></i>FR-adjustment Report</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9012')">
			                <li <c:if test="${currentFunc eq 'RF9012'}">class="active"</c:if>><a href="<c:url value="/report/ListOfPackageTourQuotationRecords/home"/>"><i class="fa fa-circle-o"></i>List of Package Tour Quotation Records</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9013')">
			                <li <c:if test="${currentFunc eq 'RF9013'}">class="active"</c:if>><a href="<c:url value="/report/SummaryStatistic/home"/>"><i class="fa fa-circle-o"></i>Summary Statistic report (Batch)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9014')">
			                <li <c:if test="${currentFunc eq 'RF9014'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfTimelog/home"/>"><i class="fa fa-circle-o"></i>Summary of Time log Activity</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9015')">
			                <li <c:if test="${currentFunc eq 'RF9015'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfTimeLogEnumerationOutcome/home"/>"><i class="fa fa-circle-o"></i>Summary of Time Log Enumeration Outcome</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9016')">
			                <li <c:if test="${currentFunc eq 'RF9016'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfWorkload/home"/>"><i class="fa fa-circle-o"></i>Summary of Quotation Workload</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9017')">
			                <li <c:if test="${currentFunc eq 'RF9017'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfSupervisoryVisitSpotCheck/home"/>"><i class="fa fa-circle-o"></i>Summary of Supervisory Visit / Spot Check</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9018')">
			                <li <c:if test="${currentFunc eq 'RF9018'}">class="active"</c:if>><a href="<c:url value="/report/OutletAmendment/home"/>"><i class="fa fa-circle-o"></i>List of Amended Outlet Information</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9019')">
			                <li <c:if test="${currentFunc eq 'RF9019'}">class="active"</c:if>><a href="<c:url value="/report/RUACaseReportOverview/home"/>"><i class="fa fa-circle-o"></i>RUA Case Report (Overview by Individual Field Officer & District)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9020')">
			                <li <c:if test="${currentFunc eq 'RF9020'}">class="active"</c:if>><a href="<c:url value="/report/RUACaseReportIndividual/home"/>"><i class="fa fa-circle-o"></i>RUA Case Report (Individual Field Officer by Outlet Type & District)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9021')">
			                <li <c:if test="${currentFunc eq 'RF9021'}">class="active"</c:if>><a href="<c:url value="/report/QuotationStatisticsReportByQuotation/home"/>"><i class="fa fa-circle-o"></i>Quotation Statistics Report (by Quotation)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9022')">
			                <li <c:if test="${currentFunc eq 'RF9022'}">class="active"</c:if>><a href="<c:url value="/report/QuotationStatisticsReportByVariety/home"/>"><i class="fa fa-circle-o"></i>Quotation Statistics Report (by Variety)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9023')">
			                <li <c:if test="${currentFunc eq 'RF9023'}">class="active"</c:if>><a href="<c:url value="/report/QuotationTimeSeries/home"/>"><i class="fa fa-circle-o"></i>Quotation Time Series Report</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9024')">
			                <li <c:if test="${currentFunc eq 'RF9024'}">class="active"</c:if>><a href="<c:url value="/report/SupermarketProductReview/home"/>"><i class="fa fa-circle-o"></i>Supermarket Product Review</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9025')">
			                <li <c:if test="${currentFunc eq 'RF9025'}">class="active"</c:if>><a href="<c:url value="/report/SummaryItinerary/home"/>"><i class="fa fa-circle-o"></i>Reconciliation Check of Itinerary Summary</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9026')">
			                <li <c:if test="${currentFunc eq 'RF9026'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfQuotations/home"/>"><i class="fa fa-circle-o"></i>Summary of Quotations (by District & Outlet Type)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9027')">
			                <li <c:if test="${currentFunc eq 'RF9027'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfProgressReportByFieldOfficer/home"/>"><i class="fa fa-circle-o"></i>Summary of Progress Report (by Individual Field Officer)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9028')">
			                <li <c:if test="${currentFunc eq 'RF9028'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfNightSession/home"/>"><i class="fa fa-circle-o"></i>Summary of Night Sessions</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9029')">
			                <li <c:if test="${currentFunc eq 'RF9029'}">class="active"</c:if>><a href="<c:url value="/report/ExperienceSummary/home"/>"><i class="fa fa-circle-o"></i>Experience Summary</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9030')">
			                <li <c:if test="${currentFunc eq 'RF9030'}">class="active"</c:if>><a href="<c:url value="/report/CalendarSummary/home"/>"><i class="fa fa-circle-o"></i>Calendar Summary (by Individual Field Officer)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9031')">
			                <li <c:if test="${currentFunc eq 'RF9031'}">class="active"</c:if>><a href="<c:url value="/report/AllocationTransferInTransferOutReallocationRecords/home"/>"><i class="fa fa-circle-o"></i>Allocation/Transfer in/Transfer out/Reallocation records</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9032')">
			                <li <c:if test="${currentFunc eq 'RF9032'}">class="active"</c:if>><a href="<c:url value="/report/AssignmentAllocationSummary/home"/>"><i class="fa fa-circle-o"></i>Assignment allocation summary</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9033')">
			                <li <c:if test="${currentFunc eq 'RF9033'}">class="active"</c:if>><a href="<c:url value="/report/SummaryStatisticsOfPriceRelatives/home"/>"><i class="fa fa-circle-o"></i>Summary Statistics of Price Relatives (PR)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9034')">
			                <li <c:if test="${currentFunc eq 'RF9034'}">class="active"</c:if>><a href="<c:url value="/report/PECheckSummary/home"/>"><i class="fa fa-circle-o"></i>PE Check Summary</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9035')">
			                <li <c:if test="${currentFunc eq 'RF9035'}">class="active"</c:if>><a href="<c:url value="/report/IndividualQuotationRecord/home"/>"><i class="fa fa-circle-o"></i>Information of Individual Quotation Records</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9036')">
			                <li <c:if test="${currentFunc eq 'RF9036'}">class="active"</c:if>><a href="<c:url value="/report/TravellingClaimForm/home" />"><i class="fa fa-circle-o"></i>Claim for Reimbursement of Travelling Expenses on Duty Journeys by Public Transport under CSR 723</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9037')">
			                <li <c:if test="${currentFunc eq 'RF9037'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfICPQuotationsByICPCode/home"/>"><i class="fa fa-circle-o"></i>(ICP) Summary of ICP Quotations (by ICP Code)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9038')">
			                <li <c:if test="${currentFunc eq 'RF9038'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfICPQuotationsByICPType/home"/>"><i class="fa fa-circle-o"></i>(ICP) Summary of ICP Quotations (by ICP Type)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9039')">
			                <li <c:if test="${currentFunc eq 'RF9039'}">class="active"</c:if>><a href="<c:url value="/report/ApplicationOTWork/home" />"><i class="fa fa-circle-o"></i>Application for Overtime Work to be Recompensed by Time-off in Lieu(TOIL)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9040')">
			                <li <c:if test="${currentFunc eq 'RF9040'}">class="active"</c:if>><a href="<c:url value="/report/ApplicationTimeOff/home" />"><i class="fa fa-circle-o"></i>Application for Time-off in Lieu</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9041')">
			                <li <c:if test="${currentFunc eq 'RF9041'}">class="active"</c:if>><a href="<c:url value="/report/FieldworkOutputByDistrict/home"/>"><i class="fa fa-circle-o"></i>Fieldwork Output Report (by District)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9042')">
			                <li <c:if test="${currentFunc eq 'RF9042'}">class="active"</c:if>><a href="<c:url value="/report/ProductCycleReportByVariety/home"/>"><i class="fa fa-circle-o"></i>Product Cycle Report (by Variety)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9043')">
			                <li <c:if test="${currentFunc eq 'RF9043'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfTimeoffOvertime/home"/>"><i class="fa fa-circle-o"></i>Summary of Time off / Over time (by Individual Field Officer)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9044')">
			                <li <c:if test="${currentFunc eq 'RF9044'}">class="active"</c:if>><a href="<c:url value="/report/FieldworkOutputByOutletType/home"/>"><i class="fa fa-circle-o"></i>Fieldwork Output Report (by Outlet Type)</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9045')">
			                <li <c:if test="${currentFunc eq 'RF9045'}">class="active"</c:if>><a href="<c:url value="/report/InformationSpotCheckForm/home" />"><i class="fa fa-circle-o"></i>Spot Check Forms</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9046')">
			                <li <c:if test="${currentFunc eq 'RF9046'}">class="active"</c:if>><a href="<c:url value="/report/InformationSupervisoryVisitForm/home"/>"><i class="fa fa-circle-o"></i>Supervisory Visit Forms</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9047')">
			                <li <c:if test="${currentFunc eq 'RF9047'}">class="active"</c:if>><a href="<c:url value="/report/InformationTimeLogForm/home"/>"><i class="fa fa-circle-o"></i>Time Log Forms</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9048')">
			                <li <c:if test="${currentFunc eq 'RF9048'}">class="active"</c:if>><a href="<c:url value="/report/QuotationRecordImputationReport/home"/>"><i class="fa fa-circle-o"></i>Quotation Record Imputation Report</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9049')">
			                <li <c:if test="${currentFunc eq 'RF9049'}">class="active"</c:if>><a href="<c:url value="/report/SummaryOfVerificationCases/home"/>"><i class="fa fa-circle-o"></i>Summary of Verification Cases</a></li>
			            </sec:authorize>
	                    <sec:authorize access="hasRole('RF9050')">
			                <li <c:if test="${currentFunc eq 'RF9050'}">class="active"</c:if>><a href="<c:url value="/report/CheckTheChecker/home"/>"><i class="fa fa-circle-o"></i>"Check the Checker" Report</a></li>
			            </sec:authorize>
		            </ul>
	             </li>
             </sec:authorize>
             
             
              
            <sec:authorize access="hasRole('UF2301')">
	             <li <c:if test="${currentFunc eq 'UF2301'}">class="active"</c:if>>
	             	<a href="<c:url value='/ManualAndUserGuide/home' />">
		            	<i class="glyphicon glyphicon-book"></i><span>Manual and User Guide Maintenance</span>
		            </a>
	             </li>
             </sec:authorize>
             
          </ul>
        </section>
        <!-- /.sidebar -->
      </aside>
</c:if>


      <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper" <c:if test="${not empty plainLayout and plainLayout}">style="margin-left:0px !important"</c:if>>
      	<div id="MessageRibbon">
		<c:if test="${FAIL_MESSAGE != null}">
			<div style="overflow: auto">
				<div class="alert alert-danger alert-dismissable" style="margin: 15px 15px 0 15px">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
					<h4>
						<i class="icon fa fa-warning"></i> Alert!
					</h4>
					${FAIL_MESSAGE}
				</div>
			</div>
		</c:if>
		<c:if test="${SUCCESS_MESSAGE != null}">
			<div style="overflow: auto">
				<div class="alert alert-success alert-dismissable" style="margin: 15px 15px 0 15px">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
					<h4>
						<i class="icon fa fa-check"></i> Information
					</h4>
					${SUCCESS_MESSAGE}
				</div>
			</div>
		</c:if>
		</div>
      	<jsp:doBody/>
      </div><!-- /.content-wrapper -->
	  
<c:if test="${empty plainLayout or !plainLayout}" >
      <footer class="main-footer">
      	&nbsp;
       <div class="pull-right hidden-xs">        
          <a href="#" class="">Back to top</a>
        </div>
      	
      <!-- 
        <div class="pull-right hidden-xs">        
          <b>Version</b> 2.3.0
        </div>
        <strong>Copyright &copy; 2014-2015 <a href="http://almsaeedstudio.com">Almsaeed Studio</a>.</strong> All rights reserved.
         -->
      </footer>
</c:if>

    </div><!-- ./wrapper -->
	
	<script src="<c:url value='/resources/js/app.js'/>"></script>
    
    <script src="<c:url value='/resources/js/common.js'/>"></script>
</body>
</html>
