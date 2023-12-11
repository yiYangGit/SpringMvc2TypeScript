//此代码为生成的代码请勿修改 !!!
import {MetaField} from '../../../example/dev/meta/fields/MetaField'
import {Observable} from 'rxjs';

export interface UpdateObj <U extends any> {

getByID(keyID: number): Observable<U> ;

getUpdateFieldSchema(): Observable<MetaField[]> ;

update(keyID: number, dataModel: U): Observable<void> ;

}

