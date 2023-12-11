//此代码为生成的代码请勿修改 !!!
import {MetaField} from '../../../example/dev/meta/fields/MetaField'
import {Observable} from 'rxjs';

export interface DetailObj <D extends any> {

getDetailFieldSchema(): Observable<MetaField[]> ;

queryByID(keyID: number): Observable<D> ;

}

