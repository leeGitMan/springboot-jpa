package jpabook2.jpashop2.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;



@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원명은 필수입니다.")
    private String name;


    private String city;
    private String street;
    private String zipcode;

}
