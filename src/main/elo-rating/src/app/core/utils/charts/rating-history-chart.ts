import { Match } from './../../../matches/shared/match.model';
import { ChartBuilder } from "app/core/utils/charts/chart-builder";

export class RatingHistoryChart extends ChartBuilder {
  constructor(private matches: Match[], private playerId: string) {
    super();
  }

  public buildTitle() {
    this.chart.title = {text: 'Rating history'};
  }

  public buildSeries() {
    let data = [['default', 1000]];
    this.matches.forEach(match => {
      let opponent = this.getOpponent(match);
      let rating = match.ratings[this.playerId];
      data.push([opponent, rating]);
    });
    this.chart.series.push({type: 'line', name: 'Rating', data: data});
  }  

  private getOpponent(match: Match): string {
    if (match.playerOne == undefined || match.playerTwo == undefined) return 'deleted player';
    return (match.playerOne.id == this.playerId 
      ? match.playerTwo.username : match.playerOne.username);
  }
}