import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IClient } from 'app/shared/model/client.model';

type EntityResponseType = HttpResponse<IClient>;
type EntityArrayResponseType = HttpResponse<IClient[]>;

@Injectable({ providedIn: 'root' })
export class ClientService {
  public resourceUrl = SERVER_API_URL + 'api/clients';

  constructor(protected http: HttpClient) {}

  create(client: IClient): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(client);
    return this.http
      .post<IClient>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(client: IClient): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(client);
    return this.http
      .put<IClient>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IClient>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IClient[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(client: IClient): IClient {
    const copy: IClient = Object.assign({}, client, {
      dateCreation: client.dateCreation && client.dateCreation.isValid() ? client.dateCreation.format(DATE_FORMAT) : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateCreation = res.body.dateCreation ? moment(res.body.dateCreation) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((client: IClient) => {
        client.dateCreation = client.dateCreation ? moment(client.dateCreation) : undefined;
      });
    }
    return res;
  }
}
