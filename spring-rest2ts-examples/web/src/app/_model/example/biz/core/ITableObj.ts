//此代码为生成的代码请勿修改 !!!
import {DetailObj} from '../../../example/biz/core/DetailObj'
import {InsertObj} from '../../../example/biz/core/InsertObj'
import {QueryObj} from '../../../example/biz/core/QueryObj'
import {UpdateObj} from '../../../example/biz/core/UpdateObj'
import {Observable} from 'rxjs';

export interface ITableObj <I extends any, U extends any, Q extends any, D extends any> extends  DetailObj<D> , InsertObj<I> , QueryObj<Q> , UpdateObj<U> {

delete(keyIDs: number[]): Observable<void> ;

}

