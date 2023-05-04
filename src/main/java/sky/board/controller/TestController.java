package sky.board.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("commit")
    public String TestCommit () {
        System.out.println("0 = " + 0);
        return "hihi";
    }

}
