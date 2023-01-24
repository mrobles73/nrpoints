package com.rtek.nrpoints.controllers;

import com.rtek.nrseasonpts.FullSeasonDriver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.rtek.nrseasonpts.utils.NRUtils;
import com.rtek.nrseasonpts.CupSeason;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AppController {

    private CupSeason season;

    @RequestMapping("/test")
    public String getTest(Model model) {
        model.addAttribute("name", "Miguel");
        return "hometest";
    }

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
            season = NRUtils.getSeason(yearInt, series, fileText);
        } catch (Exception e) {
            season = null;
            String errorMessage = "";
            if(e instanceof IndexOutOfBoundsException) {
                errorMessage = "Improper file format. Please try again.";

            } else {
                errorMessage = "Unable to read files. Please try again";
            }
            model.addAttribute("errorMessage", errorMessage);
            return "index";
        }

        List<List<FullSeasonDriver>> standings = season.getAllStandings();
        model.addAttribute("pageTitle", "Season");
        model.addAttribute("year", year);
        model.addAttribute("raceCount", standings.size());
        model.addAttribute("raceLimit", 36); //will be season.getRaceCount();
        model.addAttribute("standings", standings);
        return "season";
    }

    @RequestMapping(value = "/addrace", method = POST)
    public String postSendFiles(@RequestParam("race-file-text") String raceFilesText, @RequestParam("race-file-text-two") Optional<String> raceFilesTextTwo, Model model) {
        raceFilesText += raceFilesTextTwo.orElse("");
        String[] fileText = raceFilesText.trim().split("((?<=</HTML>))");
        if(this.season != null) {
            try {
                List<List<FullSeasonDriver>> standings = NRUtils.addRacesToSeason(this.season, fileText);
                model.addAttribute("standings", standings);
            } catch (Exception e) {
                String errorMessage = "";
                if(e instanceof IndexOutOfBoundsException) {
                    errorMessage = "Improper file format. Please try again.";

                } else {
                    errorMessage = "Unable to read files. Please try again";
                }
                model.addAttribute("message", errorMessage);
                return "fragments/message";
            }
        }

        return "fragments/standings-table";
    }

    @ResponseBody
    private String getError(String errorMessage) {
        return errorMessage;
    }

}
