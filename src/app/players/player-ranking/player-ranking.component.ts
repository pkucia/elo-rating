import { ActivatedRoute } from '@angular/router';
import { PlayerService } from './../shared/player.service';
import { Player } from './../shared/player.model';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-player-ranking',
  templateUrl: './player-ranking.component.html',
  styleUrls: ['./player-ranking.component.css']
})
export class PlayerRankingComponent implements OnInit {
  leagueId: string;
  rankedPlayers: Player[];

  constructor(
    private playerService: PlayerService,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.getLeagueId();
    this.getRanking();
  }

  getLeagueId() {
    this.route.params.map(p => p['league_id'])
      .forEach(league_id => this.leagueId = league_id);
  }

  getRanking() {
    this.playerService.getRanking(this.leagueId)
      .then(players => this.rankedPlayers = players);
  }

  hasRankedPlayers(): boolean {
    return this.rankedPlayers != undefined && this.rankedPlayers.length > 0;
  }
}