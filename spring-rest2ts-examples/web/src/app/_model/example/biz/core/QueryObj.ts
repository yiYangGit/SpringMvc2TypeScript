//此代码为生成的代码请勿修改 !!!
import {TbCond} from '../../../example/biz/TbCond'
import {MetaField} from '../../../example/dev/meta/fields/MetaField'
import {Observable} from 'rxjs';

export interface QueryObj <Q extends any> {

getQueryCondFieldSchema(): Observable<MetaField[]> ;

getQueryFieldSchema(): Observable<MetaField[]> ;

getTotal(conds: TbCond[]): Observable<number> ;

query(start: number, max: number, orderBy: string, bAsc: boolean, conds: TbCond[]): Observable<Q[]> ;

}

