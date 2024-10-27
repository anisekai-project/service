package me.anisekai.modules.shizue.services;

import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.enums.SeasonalSelectionState;
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

    public ISeasonalSelection createNewSelection(String name, long votes) {

        SeasonalSelection seasonalSelection = this.seasonalSelectionService.open(name);

        if (seasonalSelection.getAnimes().size() <= votes) {
            return this.seasonalSelectionService.mod(
                    seasonalSelection.getId(),
                    item -> item.setState(SeasonalSelectionState.AUTO_CLOSED)
            );
        }

        this.seasonalVoterService.create(seasonalSelection, votes);
        return this.seasonalSelectionService.fetch(seasonalSelection.getId());
    }

}
