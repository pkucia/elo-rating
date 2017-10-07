import { Observable } from 'rxjs/Observable';
import { User } from './user.model';
import { Http } from '@angular/http';
import { environment } from './../../../environments/environment';
import { Injectable } from '@angular/core';
import { BaseApiService } from "../../core/shared/base-api.service";


@Injectable()
export class UserService extends BaseApiService {

  constructor(private http: Http) { 
    super();
  }

  signIn(idToken: string): Promise<User> {
    let url = `${this.url}/users/sign-in`;
    return this.http.post(url, idToken)
      .toPromise()
      .then(response => response.json() as User)
      .catch(this.handleError);
  }

  findByName(name: string): Observable<User[]> {
    let url = `${this.url}/users/find-by-name?name=${name}`;
    return this.http.get(url)
      .map(response => response.json() as User[]);
  }

  assignLeague(userId: string, leagueId: string): Promise<User> {
    let url = `${this.url}/users/${userId}/assign-league/${leagueId}`;
    return this.http.post(url, null)
      .toPromise()
      .then(response => response.json() as User)
      .catch(this.handleError);
  }

  inviteUser(currentUserId: string, userToInvite: User): Promise<User> {
    let url = `${this.url}/users/${currentUserId}/invite-user`;
    return this.http.post(url, userToInvite)
      .toPromise()
      .then(response => response.json() as User)
      .catch(this.handleError);
  }  

  verifySecurityToken(token: string): Promise<boolean> {
    let url = `${this.url}/users/verify-security-token`;
    return this.http.post(url, token)
      .toPromise()
      .then(response => response.json() as boolean)
      .catch(this.handleError);
  }

  completeInvitation(googleIdToken: string, securityToken: string): Promise<User> {
    let url = `${this.url}/users/confirm-invitation`;
    let requestBody = {googleIdToken: googleIdToken, securityToken: securityToken};
    return this.http.post(url, requestBody)
      .toPromise()
      .then(response => response.json() as User)
      .catch(this.handleError);
  }

  createPlayer(userId: string, leagueId: string): Promise<User> {
    let url = `${this.url}/users/${userId}/create-player`;
    return this.http.post(url, leagueId)
      .toPromise()
      .then(response => response.json() as User)
      .catch(this.handleError);
  }
}