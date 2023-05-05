package sky.board.web.controller.board;


import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.board.domain.board.entity.Board;
import sky.board.domain.board.form.BoardForm;
import sky.board.domain.board.service.BoardImplService;

import java.io.FileNotFoundException;
import java.util.List;


@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardImplService boardImplService;



    @GetMapping
    public String boardList (@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue
            = "10") int end, Model model) {
        List<Board> boardList = boardImplService.findByList(page, end);
        model.addAttribute("List",boardList);
        return "board";
    }

    @GetMapping("/{id}")
    public String getBoard (@PathVariable Long id,Model model) throws FileNotFoundException {

        Board board = boardImplService.findById(id);
        if (board == null) {
            throw new FileNotFoundException();
        }
        model.addAttribute("board",board);
        return "boardDetail";
    }

    @GetMapping("/add")
    public String boardForm (@ModelAttribute BoardForm boardForm, Model model) {
        return "boardForm";
    }

    @PostMapping("/add")
    public String addBoard (@Validated @ModelAttribute BoardForm boardForm, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        return "redirect:/boardDetail";
    }


}
