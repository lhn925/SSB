package sky.board.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("commit-test2")
    public String TestCommit1 () {
        System.out.println("0 = " + 0);
        return "iuiu";
    }

    @GetMapping("commit")
    public String TestCommit2 () {
        System.out.println("0 = " + 0);
        return "hihi";
    }
}
