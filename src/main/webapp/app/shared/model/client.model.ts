import { Moment } from 'moment';

export interface IClient {
  id?: number;
  nom?: string;
  telephone?: string;
  solde?: number;
  isActif?: boolean;
  photoContentType?: string;
  photo?: any;
  dateCreation?: Moment;
}

export class Client implements IClient {
  constructor(
    public id?: number,
    public nom?: string,
    public telephone?: string,
    public solde?: number,
    public isActif?: boolean,
    public photoContentType?: string,
    public photo?: any,
    public dateCreation?: Moment
  ) {
    this.isActif = this.isActif || false;
  }
}
