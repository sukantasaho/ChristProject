package com.christ.erp.services.dto.account;

import java.math.BigDecimal;

import com.christ.erp.services.dto.common.SelectDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccFeeHeadsAccountDTO {
	private int id;
	private Integer accFeeHeadsId; //parent id
	private SelectDTO erpCampusDTO;
	private String sapCode;
	private SelectDTO accAccountsDTO;
	private SelectDTO erpCurrencyDTO;
	private BigDecimal amount;
	private BigDecimal amountInterNational;
	
}
