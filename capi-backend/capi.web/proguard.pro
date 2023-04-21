-keeppackagenames **.security,**.web,**.entity,**.dao,**.generator,**.timertask,**.interceptor,**.tag,**.system,**.wfmservice,**.common,**.jdbc,com.kinetix.api,com.kinetix.controller,capi.dal,capi.service,com.kinetix.component,com.kinetix.batch

-keep class **.security.**{*;}
-keep class **.web.**{*;}
-keep class **.entity.**{*;}
-keep class **.dao.**{*;}
-keep class **.timertask.**{*;}
-keep class **.interceptor.**{*;}
-keep class **.generator.**{*;}
-keep class **.tag.**{*;}
-keep class **.common.**{*;}
-keep class **.system.**{*;}
-keep class **.wfmservice.**{*;}
-keep class **.DisableCacheFilter{*;}
-keep class **.CAPIBatchJob{*;}
-keep class **.DatabaseConnectionMonitorTask{*;}

-keepclassmembers class **.security.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.dao.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.web.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.entity.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.timertask.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.interceptor.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.generator.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.tag.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.system.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.common.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.wfmservice.** {  
    <fields>;
    <methods>; 
}
-keepclassmembers class **.model.** {  
    <fields>;
    <methods>; 
}

-keepclassmembers class capi.service.CommonService {  
    <fields>;
    <methods>; 
}