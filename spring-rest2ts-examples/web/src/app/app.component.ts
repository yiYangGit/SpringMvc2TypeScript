import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {TestUserManager} from "./_model/example/biz/TestUserManager";
import {UserProfileDTO} from "./_model/example/biz/UserProfileDTO";
import {OrderPaymentStatus} from "./_model/example/model/enums/OrderPaymentStatus";
import {TestUserManager2} from "./_model/example/biz/TestUserManager2";
import {PersonDTO} from "./_model/example/model/PersonDTO";

declare var jQuery: any;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})


export class AppComponent implements OnInit{
  title = 'app works!';

  constructor(private titleService: Title,
              router: Router,
              activatedRoute: ActivatedRoute,
             private testUser:TestUserManager2
  ) {
    router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const title = this.getTitle(router.routerState, router.routerState.root).join(' | ');
        titleService.setTitle(title);
      }
    });
  }

  getTitle(state, parent) {
    const data = [];
    if (parent && parent.snapshot.data && parent.snapshot.data.title) {
      data.push(parent.snapshot.data.title);
    }

    if (state && parent) {
      data.push(... this.getTitle(state, state.firstChild(parent)));
    }
    return data;
  }

  public setTitle(newTitle: string) {
    this.titleService.setTitle(newTitle);
  }

  ngOnInit(): void {
    // this.testUser.delete([1]).subscribe(value => {
    //   console.log(value);
    // })
    let userProfileDTO = new UserProfileDTO();
    userProfileDTO.userLogin = "1q2356789";
    userProfileDTO.userData = new PersonDTO();
    this.testUser.getByOwn({
      "1q23": userProfileDTO
    }).subscribe(value => {
      console.log(value);
      console.log(value["1q23"].id == null);
    },error => {
      console.log(error);
      console.log(error.error);
    });
    if (true) {
      return;
    }
    let map = new Map<UserProfileDTO, UserProfileDTO>();
    map.set(new UserProfileDTO(), new UserProfileDTO());
    console.log(map);
    console.log(JSON.stringify(map));
    // let req = {
    //   "123": OrderPaymentStatus.UNPAID
    // };
    // console.log(this.testUser.getByOwn2);
    // this.testUser.getByOwn2(req).subscribe(value => {
    //   console.log(req["123"] == value["123"]);
    //   console.log(value);
    // });

  }
}
