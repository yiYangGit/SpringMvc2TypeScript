//此代码为生成的代码请勿修改 !!!
import {TbCond} from '../../example/biz/TbCond'
import {TestUser2} from '../../example/biz/TestUser2'
import {TestUser3} from '../../example/biz/TestUser3'
import {CategoryDTO} from '../../example/model/CategoryDTO'
import {BaseDTO} from '../../example/model/core/BaseDTO'

export class CategoryDTO$ProductDTO extends BaseDTO {
      name: number;
      barcode: string;
      websiteURI: any;
      expirationDate: any;
      tags: {[key: string] :TestUser2<TestUser3<number> , TbCond> };
      categoryDTO: CategoryDTO;
      translationsMap: {[key: string] :string};
}

