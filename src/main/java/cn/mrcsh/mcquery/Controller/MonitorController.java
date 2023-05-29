package cn.mrcsh.mcquery.Controller;

import cn.mrcsh.mcquery.Cache.Cunt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @GetMapping("/count")
    public Object count(){
        return Cunt.invokeCounts;
    }
}
