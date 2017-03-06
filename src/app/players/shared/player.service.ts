import { Http, Headers } from '@angular/http';
import { PLAYERS } from './../../testing/player-stubs';
import { Player } from './player.model';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import 'rxjs/add/operator/toPromise';


@Injectable()
export class PlayerService {

  private url = environment.serverUrl + '/tournaments/';
  private headers = new Headers({'Content-Type': 'application/json'});

  constructor(private http: Http) { }

  getPlayers(tournamentId: string): Promise<Player[]> {
    let url = `${this.url}/${tournamentId}/players`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as Player[])
      .catch(this.handleError);
  }

  addPlayer(tournamentId: string, player: Player): Promise<Player> {
    let url = `${this.url}/${tournamentId}/players`;
    return this.http.post(url, JSON.stringify(player), {headers: this.headers})
      .toPromise()
      .then(response => response.json() as Player)
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occured', error);
    return Promise.reject(error.message || error);
  }
}