package com.rtek.nrpoints.controllers;

import com.rtek.nrseasonpts.FullSeasonDriver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import com.rtek.nrseasonpts.utils.NRUtils;
import com.rtek.nrseasonpts.Season;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AppController {

    private Season season;

    @RequestMapping({"/", "/home"})
    public String getHome(Model model) {
        model.addAttribute("pageTitle", "nrpoints");
        return "index";
    }

    @RequestMapping("/about")
    public String getAbout(Model model) {
        model.addAttribute("pageTitle", "About");
        return "about";
    }

    @RequestMapping(value = "/season", method = POST)
    public String postSendFiles(@RequestParam("series") String series, @RequestParam("season-year") String year, @RequestParam("race-files") String raceFiles, @RequestParam("race-file-text") String raceFilesText, Model model) {
        String[] fileText = raceFilesText.trim().split("((?<=</HTML>))");
        int yearInt = Integer.parseInt(year);

        try {
            season = NRUtils.getContentVersion().getSeason(yearInt, series, fileText);
            List<List<FullSeasonDriver>> standings = season.getAllStandings();
            model.addAttribute("pageTitle", "Season");
            model.addAttribute("year", year);
            model.addAttribute("raceCount", standings.size());
            model.addAttribute("raceLimit", season.getRaceCount());
            model.addAttribute("standings", standings);
            return "season";
        } catch (Exception e) {
            e.printStackTrace();
            season = null;
            String errorMessage = (e instanceof IndexOutOfBoundsException) ? "Improper file format. Please try again." : "Unable to read files. Please try again";
            model.addAttribute("errorMessage", errorMessage);
            return "index";
        }
    }

    @RequestMapping(value = "/addrace", method = POST)
    public String postSendFiles(@RequestParam("race-file-text") String raceFilesText, @RequestParam("race-file-text-two") Optional<String> raceFilesTextTwo, Model model) {
        String errorMessage = "";
        if(this.season != null) {
            try {
                raceFilesText += raceFilesTextTwo.orElse("");
                String[] fileText = raceFilesText.trim().split("((?<=</HTML>))");
                List<List<FullSeasonDriver>> standings = NRUtils.getContentVersion().addRacesToSeason(this.season, fileText);
                model.addAttribute("standings", standings);
                return "fragments/standings-table";
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = (e instanceof IndexOutOfBoundsException) ? "Improper file format. Please try again." : "Unable to read files. Please try again";
            }
        } else {
            System.out.println("ERROR: Attempt to add races to a null Season object");
            errorMessage = "Unable to add files to season. If error keeps happening try again from home page.";
        }
        model.addAttribute("message", errorMessage);
        return "fragments/message";
    }

    @RequestMapping("/*")
    public String getAll(){
        return "notfound";
    }

}
