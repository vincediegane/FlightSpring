/**
 * 
 */
package com.flight.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flight.dto.AccountDto;
import com.flight.dto.FlightDto;
import com.flight.dto.LogResponseDto;
import com.flight.dto.LoginDto;
import com.flight.models.Account;
import com.flight.models.Flight;
//import com.flight.models.FlightCriteria;
import com.flight.service.IAccountService;
import com.flight.service.IFlightService;
import com.flight.utils.JwtTokenUtil;

/**
 * @author VINCENT
 *
 */
@RestController
@RequestMapping("flight-webservices/api/v1.0/flights")
public class FlightController {

  @Autowired
  private IFlightService flightService;
  
  @Autowired
  AuthenticationManager authenticationManager;
  
  @Autowired
  JwtTokenUtil jwtTokenUtil;
  
  @Autowired
  private IAccountService accountService;
  
  @Autowired
  private MessageSource messages;
  
  private ModelMapper modelMapper;
  
  PropertyMap<Flight, FlightDto> companyFieldMapping = new PropertyMap<Flight, FlightDto>() {

	@Override
	protected void configure() {
	  map().setCompanyName(source.getCompany().getCompanyName());
	  map().setCabinDetails(source.getCompany().getCabinDetails());
	  map().setInflightInfos(source.getCompany().getInflightInfos());
	}
  };

  public FlightController(ModelMapper modelMapper) {
	  this.modelMapper = modelMapper;
	  this.modelMapper.addMappings(companyFieldMapping);
  }

  @PostMapping("/addFliflight")
  public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {
	Flight addedFlight = flightService.addFlight(flight);
    return new ResponseEntity<Flight>(addedFlight, new HttpHeaders(), HttpStatus.OK);
  }

  @GetMapping("/allFlights")
  public ResponseEntity<List<FlightDto>> getAllFlights() {
	List<FlightDto> flights = flightService.getAllFlights().stream().map(flight -> modelMapper.map(flight, FlightDto.class)).collect(Collectors.toList());
    return new ResponseEntity<List<FlightDto>>(flights, new HttpHeaders(), HttpStatus.OK);
  }
  
//  @PostMapping("/search")
//  public ResponseEntity<List<FlightDto>> searchFlight(@RequestBody FlightCriteria flightCriteria) {
//	List<FlightDto> flights = flightService.searchFlight(flightCriteria).stream().map(flight -> modelMapper.map(flight, FlightDto.class)).collect(Collectors.toList());
//    return new ResponseEntity<List<FlightDto>>(flights, new HttpHeaders(), HttpStatus.OK);
//  }

  @GetMapping("/flight/{id}")
  public ResponseEntity<?> getFlight(@PathVariable(value = "id") Long idFlight) {
	  Locale currentLocale = LocaleContextHolder.getLocale();
    Flight flight = flightService.getFlight(idFlight);
    if(flight == null) {
    	return new ResponseEntity<String>(messages.getMessage("flight.exist.msg", new Object[] {idFlight}, currentLocale), HttpStatus.NOT_FOUND);
    } else {
    	FlightDto flightDto = modelMapper.map(flight, FlightDto.class);
        return new ResponseEntity<FlightDto>(flightDto, new HttpHeaders(), HttpStatus.OK);
    }
  }
  
  @PostMapping("/register")
  public ResponseEntity<Account> addAccount(@Valid @RequestBody AccountDto accountDto) {
	  Account account = accountService.addAccount(accountDto);
	  return new ResponseEntity<Account>(account, new HttpHeaders(), HttpStatus.OK);
  }
  
  @PostMapping("/authenticate")
  public ResponseEntity<LogResponseDto> authenticate(@RequestBody LoginDto loginDto) throws Exception {
	  try {
		  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
	  } catch (BadCredentialsException e) {
		  throw new Exception("Incorrect username or password", e);
	  }
	  UserDetails userDetails = accountService.loadUserByUsername(loginDto.getUsername());
	  String jwt = jwtTokenUtil.generateJwt(userDetails);
	  return new ResponseEntity<LogResponseDto>(new LogResponseDto(jwt), new HttpHeaders(), HttpStatus.OK);
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
	  Map<String, String> errors = new HashMap<>();
	  exception.getBindingResult().getAllErrors().forEach(error -> {
		  String field = "";
	      if (error.getClass().getSimpleName().equals("ValidationObjectError")) {
			  field = ((ObjectError) error).getObjectName();
		  }
	      else if(error.getClass().getSimpleName().equals("ValidationFieldError")) {
	    	  field = ((FieldError) error).getField();
	      }
		  String message = error.getDefaultMessage();
		  errors.put(field, message);
	  });
	  return errors;
  }
}
