//此代码为生成的代码请勿修改 !!!
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {TbCond} from '../../example/biz/TbCond'
import {TestUser3} from '../../example/biz/TestUser3'
import {UserProfileDTO} from '../../example/biz/UserProfileDTO'
import {ITableObj} from '../../example/biz/core/ITableObj'
import {MetaField} from '../../example/dev/meta/fields/MetaField'
import {AddressDTO} from '../../example/model/AddressDTO'
import {OrderDTO} from '../../example/model/OrderDTO'
import {PersonDTO} from '../../example/model/PersonDTO'
import {OrderPaymentStatus} from '../../example/model/enums/OrderPaymentStatus'
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({providedIn: "root"})
export class TestUserManager implements ITableObj<OrderDTO, PersonDTO, AddressDTO, AddressDTO> {
      public static readonly h: string = "hhhh";
      public static readonly asda: string = "asda";
      public static readonly objs: TestUser3<number> [] = [{"t3":1},{"t3":1}];
      httpService: HttpClient;


 public constructor(httpService: HttpClient) {
    this.httpService = httpService;
 }

 public delete(keyIDs: number[]): Observable<void>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<void>('/download/delete', keyIDs , {headers});
 }

 public getByID(keyID: number): Observable<PersonDTO>  {
    return this.httpService.post('/download/getByID/' + keyID + '', null , {responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public getByOwn(req: {[key: string] :UserProfileDTO}): Observable<{[key: string] :UserProfileDTO}>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post('/download/getByOwn', JSON.stringify(req) , {headers, responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public getByOwn2(req: {[key: string] :OrderPaymentStatus}): Observable<{[key: string] :OrderPaymentStatus}>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post('/download/getByOwn2', JSON.stringify(req) , {headers, responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public getDetailFieldSchema(): Observable<MetaField[]>  {
    return this.httpService.post('/download/getDetailFieldSchema', null , {responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public getInsertFieldSchema(): Observable<MetaField[]>  {
    return this.httpService.post('/download/getInsertFieldSchema', null , {responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public getQueryCondFieldSchema(): Observable<MetaField[]>  {
    return this.httpService.post('/download/getQCondFieldSchema', null , {responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public getQueryFieldSchema(): Observable<MetaField[]>  {
    return this.httpService.post('/download/getQueryFieldSchema', null , {responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public getTotal(conds: TbCond[]): Observable<number>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<number>('/download/getTotal', conds , {headers});
 }

 public getUpdateFieldSchema(): Observable<MetaField[]>  {
    return this.httpService.post('/download/getUpdateFieldSchema', null , {responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public insert(insertObj: OrderDTO): Observable<void>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<void>('/download/insert', insertObj , {headers});
 }

 public query(start: number, max: number, orderBy: string, bAsc: boolean, conds: TbCond[]): Observable<AddressDTO[]>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post('/download/query/' + start + '/' + max + '/' + orderBy + '/' + bAsc + '', JSON.stringify(conds) , {headers, responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public queryByID(keyID: number): Observable<AddressDTO>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post('/download/queryByID', JSON.stringify(keyID) , {headers, responseType: 'text'}).pipe(map(res => JSON.parse(res)));
 }

 public update(keyID: number, dataModel: PersonDTO): Observable<void>  {
    const headers = new HttpHeaders().set('Content-type', 'application/json');
    return this.httpService.post<void>('/download/update/' + keyID + '', dataModel , {headers});
 }

}

