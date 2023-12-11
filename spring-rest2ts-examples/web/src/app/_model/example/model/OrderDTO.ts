//此代码为生成的代码请勿修改 !!!
import {AddressDTO} from '../../example/model/AddressDTO'
import {CategoryDTO$ProductDTO} from '../../example/model/CategoryDTO$ProductDTO'
import {PersonDTO} from '../../example/model/PersonDTO'
import {BaseDTO} from '../../example/model/core/BaseDTO'
import {OrderDeliveryStatus} from '../../example/model/enums/OrderDeliveryStatus'
import {OrderPaymentStatus} from '../../example/model/enums/OrderPaymentStatus'

export class OrderDTO extends BaseDTO {
      buyer: PersonDTO;
      deliveryAddressDTO: AddressDTO;
      productList: CategoryDTO$ProductDTO[];
      orderPaymentStatus: OrderPaymentStatus;
      orderDeliveryStatus: OrderDeliveryStatus;
      orderTimestamp: any;
}

