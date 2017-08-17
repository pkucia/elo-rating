import { Observable } from 'rxjs/Observable';
import { Match } from './../../matches/shared/match.model';
import { PlayerStats } from './player-stats.model';
import { OpponentStats } from './opponent-stats.model';
import { Http, Headers } from '@angular/http';
import { Player } from './player.model';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import 'rxjs/add/operator/toPromise';


@Injectable()
export class PlayerService {

  private url = environment.serverUrl;
  private headers = new Headers({ 'Content-Type': 'application/json' });

  constructor(private http: Http) { }

  getPlayers(leagueId: string): Promise<Player[]> {
    let url = `${this.url}/leagues/${leagueId}/players`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as Player[])
      .catch(this.handleError);
  }

  getPlayer(id: string): Promise<Player> {
    let url = `${this.url}/players/${id}`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as Player)
      .catch(this.handleError);
  }

  addPlayer(leagueId: string, player: Player): Promise<Player> {
    let url = `${this.url}/leagues/${leagueId}/players`;
    return this.http.post(url, JSON.stringify(player), { headers: this.headers })
      .toPromise()
      .then(response => response.json() as Player)
      .catch(this.handleError);
  }

  getRanking(leagueId: string): Promise<Player[]> {
    let url = `${this.url}/leagues/${leagueId}/players/ranking`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as Player[])
      .catch(this.handleError);
  }

  getPlayerStats(playerId: string): Promise<PlayerStats> {
    let url = `${this.url}/players/${playerId}/stats`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as PlayerStats)
      .catch(this.handleError);
  }

  delete(id: string): Promise<boolean> {
    let url = `${this.url}/players/${id}`;
    return this.http.delete(url)
      .toPromise()
      .then(response => response.ok)
      .catch(this.handleError);
  }

  update(player: Player): Promise<Player> {
    let url = `${this.url}/players/${player.id}`;
    return this.http.put(url, JSON.stringify(player), { headers: this.headers })
      .toPromise()
      .then(response => response.json() as Player)
      .catch(this.handleError);
  }

  getMatchForecast(playerId: string, opponentId: string): Promise<Match[]> {
    let url = `${this.url}/players/${playerId}/match-forecast/${opponentId}`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as Match[])
      .catch(this.handleError);
  }

  getOpponentsStats(playerId: string): Promise<OpponentStats[]> {
    let url = `${this.url}/players/${playerId}/opponents`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as OpponentStats[])
      .catch(this.handleError);
  }

  findByUsername(leagueId: string, username: string): Observable<Player[]> {
    let url = `${this.url}/leagues/${leagueId}/users/find-by-username?username=${username}`;
    return this.http.get(url)
      .map(response => response.json() as Player[])
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occured', error);
    return Promise.reject(error.message || error);
  }
}
