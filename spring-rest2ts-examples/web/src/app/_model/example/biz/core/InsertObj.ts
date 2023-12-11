//此代码为生成的代码请勿修改 !!!
import {MetaField} from '../../../example/dev/meta/fields/MetaField'
import {Observable} from 'rxjs';

export interface InsertObj <I extends any> {

getInsertFieldSchema(): Observable<MetaField[]> ;

insert(insertObj: I): Observable<void> ;

}

