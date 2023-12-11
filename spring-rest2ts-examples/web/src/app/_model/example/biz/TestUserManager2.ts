//此代码为生成的代码请勿修改 !!!
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {TestUserManager} from '../../example/biz/TestUserManager'
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({providedIn: "root"})
export class TestUserManager2 extends TestUserManager {

 public constructor(httpService: HttpClient) {
   super(httpService);
 }

}

