package me.anisekai.modules.shizue.services;

import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalSelection;
import me.anisekai.modules.shizue.services.data.SeasonalSelectionDataService;
import me.anisekai.modules.shizue.services.data.SeasonalVoterDataService;
import org.springframework.stereotype.Service;

@Service
public class ShizueService {

    private final SeasonalSelectionDataService seasonalSelectionService;
    private final SeasonalVoterDataService     seasonalVoterService;

    public ShizueService(SeasonalSelectionDataService seasonalSelectionService, SeasonalVoterDataService seasonalVoterService) {

        this.seasonalSelectionService = seasonalSelectionService;
        this.seasonalVoterService     = seasonalVoterService;
    }

    public ISeasonalSelection createNewSelection(String name) {

        SeasonalSelection seasonalSelection = this.seasonalSelectionService.open(name);
        this.seasonalVoterService.create(seasonalSelection);
        return this.seasonalSelectionService.fetch(seasonalSelection.getId());
    }

}
