package tr.com.srdc.cda2fhir.impl;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.NarrativeStatusEnum;
import ca.uhn.fhir.model.primitive.Base64BinaryDt;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.DecimalDt;
import ca.uhn.fhir.model.primitive.IntegerDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import java.util.TimeZone;

import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.Participant2;
import org.openhealthtools.mdht.uml.cda.Person;
import org.openhealthtools.mdht.uml.cda.StrucDocText;
import org.openhealthtools.mdht.uml.hl7.datatypes.*;
import org.openhealthtools.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.hl7.vocab.PostalAddressUse;

import tr.com.srdc.cda2fhir.DataTypesTransformer;
import tr.com.srdc.cda2fhir.ValueSetsTransformer;

/**
 * Created by mustafa on 7/21/2016.
 */
public class DataTypesTransformerImpl implements DataTypesTransformer {
	ValueSetsTransformer vst = new ValueSetsTransformerImpl();
	public AddressDt AD2Address(AD ad) {
	    
	    if(ad == null || ad.isSetNullFlavor()) return null;
	    else{
	        
	        AddressDt address = new AddressDt();
	        
	        if( !ad.getUses().isEmpty() && ad.getUses() != null ){
	        	
	        	
	        	
	        	// We get the address.type and address.use from the list ad.uses
	        	for(PostalAddressUse postalAddressUse : ad.getUses()){
	        		// If we catch a valid value for type or use, we assign it
	        		if( postalAddressUse == PostalAddressUse.PHYS || postalAddressUse == PostalAddressUse.PST ){
	        			address.setType( vst.PostalAddressUse2AddressTypeEnum( postalAddressUse ) );
	        		} else if( postalAddressUse == PostalAddressUse.H ||
	        				postalAddressUse == PostalAddressUse.HP ||
	        				postalAddressUse == PostalAddressUse.WP ||
	        				postalAddressUse == PostalAddressUse.TMP ||
	        				postalAddressUse == PostalAddressUse.BAD ){
	        			address.setUse( vst.PostalAdressUse2AddressUseEnum( postalAddressUse ) );
	        		}
	        	}
	        }       
	        
	        if( ad.getText() != null && !ad.getText().isEmpty() ){
	        	address.setText( ad.getText() );
	        }
	        
	        if( !ad.getStreetAddressLines().isEmpty() && ad.getStreetAddressLines() != null){
	        	for(ADXP adxp : ad.getStreetAddressLines()){
	                address.addLine(adxp.getText());
	            }
	        }
	        if(!ad.getDeliveryAddressLines().isEmpty() && ad.getDeliveryAddressLines() != null){
	        	for(ADXP adxp : ad.getDeliveryAddressLines()){
	                address.addLine(adxp.getText());
	            }
	        }
	        
	        if(!ad.getCities().isEmpty() && ad.getCities() != null){
	            address.setCity(ad.getCities().get(0).getText());
	        }
	        
	        if(!ad.getCounties().isEmpty() && ad.getCounties() != null ){
	            address.setDistrict(ad.getCounties().get(0).getText());
	        }
	        
	        if(!ad.getCities().isEmpty() && ad.getCities() != null){
	            address.setCity(ad.getCities().get(0).getText());
	        }
	        
	        if(!ad.getStates().isEmpty() && ad.getStates() != null){
	            address.setState(ad.getStates().get(0).getText());
	        }
	        
	        if( !ad.getPostalCodes().isEmpty() && ad.getPostalCodes() != null){
	            address.setPostalCode(ad.getPostalCodes().get(0).getText());
	        }
	        
	        if(!ad.getCountries().isEmpty() && ad.getCounties() != null){
	            address.setCountry(ad.getCountries().get(0).getText());
	        }
	        
	        if(!ad.getUseablePeriods().isEmpty() && ad.getUseablePeriods() != null){
	            PeriodDt period = new PeriodDt();
	            DateTimeDt dateTimeStart = new DateTimeDt();
	            dateTimeStart.setValueAsString( ad.getUseablePeriods().get(0).getValue() );
	            period.setStart( dateTimeStart);
	            
	            if(ad.getUseablePeriods().get(1) != null){
	                DateTimeDt dateTimeEnd = new DateTimeDt();
	                dateTimeEnd.setValueAsString( ad.getUseablePeriods().get(1).getValue() );
	                period.setEnd(dateTimeEnd);
	            }
	            
	            address.setPeriod(period);
	
	        }
	        
	        return address;
	    }
	}//end AddressDt

	//TODO: Mustafa: This will be revisited and updated for Act.author; not any participant
	public AnnotationDt Act2Annotation(Act act){
		if( act == null || act.isSetNullFlavor() ) return null;
		else{
			AnnotationDt myAnnotationDt = new AnnotationDt();
			for(Participant2 theParticipant : act.getParticipants()){
				if(theParticipant.getTypeCode() == ParticipationType.AUT){
					//TODO: Annotation.author[x]
					// Type	Reference(Practitioner | Patient | RelatedPerson)|string
					// For now, we are getting the name of the participant as a string
					if (theParticipant.getRole().getPlayer() instanceof Person) {
						Person person = (Person)theParticipant.getRole().getPlayer();
						if( !person.getNames().get(0).getText().isEmpty() ){
							myAnnotationDt.setAuthor( new StringDt(person.getNames().get(0).getText()) );
						}
					}
					myAnnotationDt.setTime( IVL_TS2Period(act.getEffectiveTime()).getStartElement() );
					//TODO: While setTime is waiting a parameter as DateTime, act.effectiveTime gives output as IVL_TS (Interval)
					//In sample XML, it gets the effective time as the low time
					//Check if it is ok
					if( !act.getText().isSetNullFlavor() && !act.getText().toString().isEmpty() )
						myAnnotationDt.setText(act.getText().toString());
				}
			}
			return myAnnotationDt;
		}
	}
	
	public Base64BinaryDt BIN2Base64Binary(BIN bin){
    	
    	if(bin.getRepresentation().getLiteral()!=null)
    	{
    		// TODO: It doesn't seem convenient. There should be a way to get the value of BIN.
    		Base64BinaryDt base64BinaryDt = new Base64BinaryDt();
        	base64BinaryDt.setValue(bin.getRepresentation().getLiteral().getBytes());
        	return base64BinaryDt;
    	}
    	else
    	{
    		return null;
    	}
    	
    }
	
	public BooleanDt BL2Boolean(BL bl){
	     	return (  bl == null || bl.isSetNullFlavor() ) ? null : new BooleanDt(bl.getValue());
	}
	
	public CodeableConceptDt CD2CodeableConcept(CD cd) {
        if( cd == null || cd.isSetNullFlavor() ) return null;
        else{
        	//List<CodingDt> myCodingDtList = new ArrayList<CodingDt>();
        	CodeableConceptDt myCodeableConceptDt = new CodeableConceptDt();
        	for(CD myCd : cd.getTranslations() ){

        		CodingDt codingDt = new CodingDt();
        		boolean isEmpty = true;
            	
            	if( myCd.getCodeSystem() != null ){
            		codingDt.setSystem( vst.oid2Url(myCd.getCodeSystem()) );
            		isEmpty = false;
            	}
            	if( myCd.getCode() !=null ){
            		codingDt.setCode( myCd.getCode() );
            		isEmpty = false;
            	}
            	if( myCd.getCodeSystemVersion() !=null ){
            		codingDt.setVersion( myCd.getCodeSystemVersion() );
            		isEmpty = false;
            	}
            	if( myCd.getDisplayName() != null ){
            		codingDt.setDisplay( myCd.getDisplayName() );
            		isEmpty = false;
            	}
            	if (isEmpty == false)
            		myCodeableConceptDt.addCoding( codingDt );
        	}
        	
        	boolean isEmpty = true;
        	
        	CodingDt codingDt = new CodingDt();
        	if( cd.getCodeSystem() != null ){
        		codingDt.setSystem(vst.oid2Url(cd.getCodeSystem())  );
        		isEmpty = false;
        	}
        	if( cd.getCode() !=null ){
        		codingDt.setCode( cd.getCode() );
        		isEmpty = false;
        	}
        	if( cd.getCodeSystemVersion() !=null ){
        		codingDt.setVersion( cd.getCodeSystemVersion() );
        		isEmpty = false;
        	}
        	if( cd.getDisplayName() != null ){
        		codingDt.setDisplay( cd.getDisplayName() );
        		isEmpty = false;
        	}
        	if (isEmpty == false)
        		myCodeableConceptDt.addCoding( codingDt );
        	
        	return myCodeableConceptDt;
        }
    }
	
	public CodingDt CV2Coding(CV cv) {
    	if(cv == null || cv.isSetNullFlavor()) return null;
    	else{
	    	CodingDt codingDt= new CodingDt();
	    	codingDt.setSystem(cv.getCodeSystem());
	    	codingDt.setVersion(cv.getCodeSystemVersion());
	    	codingDt.setCode(cv.getCode());
	    	codingDt.setDisplay(cv.getDisplayName());
	    	// Mapping from Coding.userSelected to CD.codingRationale doesn't exist
	        return codingDt;
    	}
    }
	
	public AttachmentDt ED2Attachment(ED ed) {
		if(ed==null || ed.isSetNullFlavor())
		{
			return null;
		}
		else
		{
			AttachmentDt attachmentDt = new AttachmentDt();
			if(ed.isSetMediaType() && ed.getMediaType()!=null && !ed.getMediaType().isEmpty())
			{
				attachmentDt.setContentType(ed.getMediaType());
			}
			if(ed.getLanguage()!=null && !ed.getLanguage().isEmpty())
			{
				attachmentDt.setLanguage(ed.getLanguage());
			}
			if( !ed.getText().isEmpty() && ed.getText() != null )
			{
				attachmentDt.setData( ed.getText().getBytes() );				
			}
			if( ed.getReference()!=null )
			{
				attachmentDt.setUrl(ed.getReference().getValue());
			}
			if(ed.getIntegrityCheck()!=null)
			{
				attachmentDt.setHash(ed.getIntegrityCheck());
			}
			// ED.title.data doesn't exist
			// Therefore, couldn't map ED.title.data <=> Attachment.title
			return attachmentDt;
		}
	}//end attachmentDt
	
	public HumanNameDt EN2HumanName(EN en) {
		
		if( en != null && !en.isSetNullFlavor()){
			
			HumanNameDt myHumanName = new HumanNameDt();
			
			if( en.getText() != null && !en.getText().isEmpty()){
				myHumanName.setText( en.getText() );
			}
			
			if(en.getUses() != null && !en.getUses().isEmpty()){
				ValueSetsTransformerImpl VSTI = new ValueSetsTransformerImpl();
				myHumanName.setUse( VSTI.EntityNameUse2NameUseEnum(en.getUses().get(0)) );
			}
			
			if(en.getFamilies() != null && !en.getFamilies().isEmpty()){
				for(ENXP element: en.getFamilies()){
					myHumanName.addFamily( element.getText() );
				}
			}
			if(en.getGivens() != null && !en.getGivens().isEmpty()){
				for(ENXP element: en.getGivens()){
					myHumanName.addGiven( element.getText() );
				}
			}
			if(en.getPrefixes() != null && !en.getPrefixes().isEmpty()){
				for(ENXP element: en.getPrefixes( )){
					myHumanName.addPrefix( element.getText() );
				}
			}
			if(en.getSuffixes() != null && !en.getSuffixes().isEmpty()){
				for(ENXP element: en.getSuffixes()){
					myHumanName.addSuffix( element.getText() );
				}
			}
			
			if( en.getValidTime() != null && !en.getValidTime().isSetNullFlavor() ){
				PeriodDt periodDt = IVL_TS2Period( en.getValidTime() );
				myHumanName.setPeriod(periodDt);
			}
			
			return myHumanName;
						
		}
						
	return null;
	
	}
	
	public IdentifierDt II2Identifier(II ii) {
		
		if( ii != null  && !ii.isSetNullFlavor()){
			IdentifierDt identifierDt = new IdentifierDt();
			
//			if(ii.getRoot() != null){
//				if( !ii.getRoot().isEmpty() )
//				{
//					identifierDt.setSystem( ii.getExtension() );
//				}
//			}//end if
			
			if(ii.getRoot() != null && !ii.getRoot().isEmpty()){

					identifierDt.setValue( ii.getRoot() );
			}
			
			if( ii.getAssigningAuthorityName() != null){
				ResourceReferenceDt resourceReference = new ResourceReferenceDt( ii.getAssigningAuthorityName() );
				if( !resourceReference.isEmpty() )
					identifierDt.setAssigner( resourceReference );
			}
			
			return identifierDt;

		}
		return null;

	}//end II2Identifier
	
	public IntegerDt INT2Integer(INT myInt){
    	return (myInt == null || myInt.isSetNullFlavor() ) ? null : new IntegerDt(myInt.getValue().toString());
    }
	
	public PeriodDt IVL_TS2Period(IVL_TS ivlts) {
		if( ivlts == null || ivlts.isSetNullFlavor() ) return null;
		else{
			PeriodDt periodDt =new PeriodDt();
			
			if(ivlts.getLow() != null && !ivlts.getLow().isSetNullFlavor())
			{
				String date=ivlts.getLow().getValue();
				periodDt.setStart(String2DateTime(date));
			}
			if(ivlts.getHigh() != null && !ivlts.getHigh().isSetNullFlavor())
			{
				String date=ivlts.getHigh().getValue();
				periodDt.setEnd(String2DateTime(date));
			}
			return periodDt;
		}
		
	}
	
	public RangeDt IVL_PQ2Range(IVL_PQ ivlpq){
		if( ivlpq == null || ivlpq.isSetNullFlavor() ) return null;
		else{
			RangeDt rangeDt = new RangeDt();
			if(ivlpq.getLow()==null && ivlpq.getHigh()==null)
			{
				return rangeDt;
			}
			else
			{	
				if(ivlpq.getLow() != null){
				if(ivlpq.getLow().getValue()!=null && !ivlpq.getLow().isSetNullFlavor())
				{
					
					SimpleQuantityDt simpleQuantity=new SimpleQuantityDt();
					simpleQuantity.setValue(ivlpq.getLow().getValue().doubleValue());
					simpleQuantity.setUnit( ivlpq.getLow().getUnit() );
					rangeDt.setLow(simpleQuantity);
					
				}
				}
				if(ivlpq.getHigh() != null){
				if(ivlpq.getHigh().getValue()!=null && !ivlpq.getHigh().isSetNullFlavor())
				{
					SimpleQuantityDt simpleQuantity=new SimpleQuantityDt();
					simpleQuantity.setValue(ivlpq.getHigh().getValue().doubleValue());
					simpleQuantity.setUnit( ivlpq.getHigh().getUnit() );
					rangeDt.setHigh(simpleQuantity);
				}
				}
				return rangeDt;
			}
		}
	}

	public TimingDt PIVL_TS2Timing(PIVL_TS pivlts) {
		if(pivlts == null || pivlts.isSetNullFlavor())
			return null;

		TimingDt timing = new TimingDt();

		// period -> period
		if(pivlts.getPeriod() != null && !pivlts.getPeriod().isSetNullFlavor()) {
			TimingDt.Repeat repeat = new TimingDt.Repeat();
			timing.setRepeat(repeat);
			// period.value -> repeat.period
			if(pivlts.getPeriod().getValue() != null)
				repeat.setPeriod(pivlts.getPeriod().getValue());
			// period.unit -> repeat.periodUnits
			if(pivlts.getPeriod().getUnit() != null)
				repeat.setPeriodUnits(vst.PeriodUnit2UnitsOfTimeEnum(pivlts.getPeriod().getUnit()));
		}

		// phase -> phase
		// TODO: Necip buradan devam et


		return timing;
	}

	public QuantityDt PQ2Quantity(PQ pq)
	{
		if(pq == null || pq.isSetNullFlavor() ) return null;
		else{
			QuantityDt quantityDt = new QuantityDt();
			if(!pq.isSetNullFlavor())
			{
				if( pq.getValue() != null)
					quantityDt.setValue(pq.getValue());
				if( pq.getUnit() != null && !pq.getUnit().isEmpty())
				{
					quantityDt.setUnit(pq.getUnit());
				}
				for(PQR pqr : pq.getTranslations())
				{
					if(pqr!=null)
					{
						quantityDt.setSystem(pqr.getCodeSystem());
						quantityDt.setCode(pqr.getCode());
					}
					else
					{
						break;
					}
				}
			}//end if
			return quantityDt;
		}
		
	}

	public SimpleQuantityDt PQ2SimpleQuantityDt( PQ pq ){
		if( pq == null || pq.isSetNullFlavor() ) return null;
		else{
			SimpleQuantityDt simpleQuantity = new SimpleQuantityDt();
			
			// https://www.hl7.org/fhir/datatypes-mappings.html#simplequantity
			
			// value
			if( pq.getValue() != null ){
				simpleQuantity.setValue(pq.getValue());
			}
			
			// unit
			if( pq.getUnit() != null && !pq.getUnit().isEmpty() ){
				simpleQuantity.setUnit( pq.getUnit() );
			}
			
			// system and code
			if( pq.getTranslations() != null && !pq.getTranslations().isEmpty() ){
				for( org.openhealthtools.mdht.uml.hl7.datatypes.PQR pqr : pq.getTranslations() ){
					if( pqr != null && !pqr.isSetNullFlavor() ){
						
						// system
						simpleQuantity.setSystem( vst.oid2Url( pqr.getCodeSystem() ) );
						
						// code
						simpleQuantity.setCode( pqr.getCode() );
					}
				}
			}
			return simpleQuantity;
		}
	}
	
	public DecimalDt REAL2Decimal(REAL real){
    	return (real == null || real.isSetNullFlavor() ) ? null : new DecimalDt(real.getValue());
    }
	
	public RatioDt RTO2Ratio(RTO rto){
    	if( rto == null || rto.isSetNullFlavor() ) return null;
    	else{
    		RatioDt myRatioDt = new RatioDt();
    		if( ! rto.getNumerator().isSetNullFlavor() ) {
    			QuantityDt quantity=new QuantityDt();
    			REAL numerator= (REAL) rto.getNumerator();
    			quantity.setValue(numerator.getValue().doubleValue());
    			myRatioDt.setNumerator( quantity);
    		}
    		if( !rto.getDenominator().isSetNullFlavor() ){
    			QuantityDt quantity=new QuantityDt();
    			REAL denominator= (REAL) rto.getDenominator();
    			quantity.setValue(denominator.getValue().doubleValue());
    			myRatioDt.setDenominator(quantity);
    		}
    		return myRatioDt;
    	}
    }
	
	public StringDt ST2String(ST st){
    	return ( st == null || st.isSetNullFlavor() ) ? null : new StringDt(st.getText());
    }

	public NarrativeDt StrucDocText2Narrative(StrucDocText sdt) {
		if(sdt != null) {
			NarrativeDt narrative = new NarrativeDt();
			narrative.setDiv(StrucDocText2String(sdt));
			narrative.setStatus(NarrativeStatusEnum.ADDITIONAL);
			return narrative;
		}
		return null;
	}

	public ContactPointDt TEL2ContactPoint(TEL tel) {
		
		if( tel!=null && !tel.isSetNullFlavor()){
			
			ContactPointDt contactPointDt = new ContactPointDt();
			
			if(tel.getValue() != null ){
				String value = tel.getValue();
				String[] systemType = value.split(":");
				if( systemType.length > 1  ){
					// for the values in form tel:+1(555)555-1000
					if( systemType[0].equals("phone") || systemType[0].equals("tel") )
						contactPointDt.setSystem(ContactPointSystemEnum.PHONE);
					else if( systemType[0].equals("email") )
						contactPointDt.setSystem(ContactPointSystemEnum.EMAIL);
					else if( systemType[0].equals("fax") )
						contactPointDt.setSystem(ContactPointSystemEnum.FAX);
					else if( systemType[0].equals("http") || systemType[0].equals("https") )
						contactPointDt.setSystem(ContactPointSystemEnum.URL);
					
					contactPointDt.setValue( systemType[1] );
				}
				else if( systemType.length == 1 ){
					// for the values in form +1(555)555-5000
					contactPointDt.setValue( systemType[0] );
				}
			}
			
			PeriodDt period = new PeriodDt();
			if(!tel.getUseablePeriods().isEmpty())
			{
				DateTimeDt dateTime = new DateTimeDt();
				dateTime.setValueAsString(tel.getUseablePeriods().get(0).getValue());
				period.setStart(dateTime);
				if(tel.getUseablePeriods().get(1) != null ){
					DateTimeDt dateTime2 = new DateTimeDt();
					dateTime2.setValueAsString(tel.getUseablePeriods().get(1).getValue());
					period.setEnd(dateTime2);
				}
				contactPointDt.setPeriod(period);
			}
			
			
			
			if(!tel.getUses().isEmpty()){
				ValueSetsTransformerImpl VSTI = new ValueSetsTransformerImpl();
				contactPointDt.setUse( VSTI.TelecommunicationAddressUse2ContacPointUseEnum( tel.getUses().get(0) ) );
				
			}
			
			return contactPointDt;
		}
		
		return null;
	}
	
	public DateDt TS2Date(TS ts){
		if(ts == null || ts.isSetNullFlavor())
			return null;
		
		String dateString = ts.getValue();
		DateDt date = new DateDt();
		TemporalPrecisionEnum precision = null;
		
		/*
		 * Possible date forms
		 * YYYY: year
		 * YYYYMM: year month
		 * YYYYMMDD: year month day
		 * YYYYMMDDHHMM: year month day hour minute
		 * YYYYMMDDHHMMSS.S: year month day hour minute second
		 * YYYYMMDDHHMM+TIZO: year month day hour minute timezone
		 */
		
		// determining precision
		switch(dateString.length()) {
			case 4: // yyyy
				precision = TemporalPrecisionEnum.YEAR; break;
			case 6: // yyyymm
				precision = TemporalPrecisionEnum.MONTH; break;
			case 8: // yyyymmdd
				precision = TemporalPrecisionEnum.DAY; break;
			case 12: // yyyymmddhhmm
			case 17: // yyyymmddhhmm+tizo
				precision = TemporalPrecisionEnum.MINUTE; break;
			case 16: // yyyymmddhhmmss.s
				precision = TemporalPrecisionEnum.MILLI; break;
			default:
				precision = null;
		}
		
		// for special cases
		if(dateString.contains("."))
			precision =  TemporalPrecisionEnum.MILLI;
		
		// setting precision
		date.setPrecision(precision);
		
		// YYYYMMDDHHMM+TIZO and YYYYMMDDHHMMSS.S are special cases
		// If our case is one of them, we will treat differently
		
		if(dateString.contains(".")) {
			// get the integer starting from char '.' as the millis
			int millis = new Integer(dateString.substring(dateString.indexOf('.')+1));
			if(millis > 0 && millis < 1000) {
				while(millis*10 <1000) {
					millis *= 10;
				}
			} else {
				millis = 0;
			}
			date.setMillis(millis);
			
			// second, minute, hour, day, month, year..
			date.setSecond(new Integer(dateString.substring(12,14)));
			date.setMinute(new Integer(dateString.substring(10, 12)));
			date.setHour(new Integer(dateString.substring(8,10)));
			date.setDay(new Integer(dateString.substring(6,8)));
			date.setMonth(new Integer(dateString.substring(4,6))-1); // 0-index
			date.setYear(new Integer(dateString.substring(0,4)));
			
		} else if(dateString.contains("+") || dateString.contains("-")) {
			// getting the timezone part
			date.setTimeZone(TimeZone.getTimeZone("GMT"+dateString.substring(12)));
			
			// minute, hour, day, month, year..
			date.setMinute(new Integer(dateString.substring(10, 12)));
			date.setHour(new Integer(dateString.substring(8,10)));
			date.setDay(new Integer(dateString.substring(6,8)));
			date.setMonth(new Integer(dateString.substring(4,6))-1); // 0-index
			date.setYear(new Integer(dateString.substring(0,4)));
		} else {
			// since there are strange situtations where the index changes upon the precision, we set every value in its precision block
			switch(precision) {
				case MINUTE: 
					date.setMinute(new Integer(dateString.substring(10,12)));
					date.setHour(new Integer(dateString.substring(8,10)));
					date.setDay(new Integer(dateString.substring(6,8)));
					date.setMonth(new Integer(dateString.substring(4,6))-1);
					date.setYear(new Integer(dateString.substring(0,4))); 
					break;
				case DAY:
					date.setDay(new Integer(dateString.substring(6,8)));
					date.setMonth(new Integer(dateString.substring(4,6))-1);
					date.setYear(new Integer(dateString.substring(0,4))); 
					break;
				case MONTH:
					date.setMonth(new Integer(dateString.substring(4,6)));
					date.setYear(new Integer(dateString.substring(0,4))); 
					break;
				case YEAR:
					// there is a strange situtation
					date.setYear(new Integer(dateString.substring(0,4))+1); 
					break;
				default:
					date = null;
			}
		}
		return date;
	}

	public DateTimeDt TS2DateTime(TS ts) {
		if(ts == null || ts.isSetNullFlavor() ) return null;
		else{
			if( ts.getValue() == null )
				return new DateTimeDt();
			
			String date=ts.getValue();
			if(date.length()>8)
				date=date.substring(0,8);
			return String2DateTime(date);
		}
	}
	
	public InstantDt TS2Instant(TS ts) {
		if(ts==null || ts.isSetNullFlavor())
			return null;
		else{
			if(ts.getValue()==null)
				return new InstantDt();
			String date=ts.getValue();
			return dateParserInstant(date);
		}
	}

	public UriDt URL2Uri(URL url){
    	return (url == null || url.isSetNullFlavor()) ? null : new UriDt(url.getValue());
    }

	@Override
	public DateTimeDt String2DateTime(String date)
	{
		DateTimeDt dateTimeDt = new DateTimeDt();
		boolean isPrecisionSet=false;
		boolean dayExist=false;
		boolean monthExist=false;
		switch(date.length())
		{	
			default:
				if(date.length()>12)
				{
					if(!isPrecisionSet)
					{
						dateTimeDt.setPrecision(TemporalPrecisionEnum.MINUTE);
						isPrecisionSet=true;
					}
					/*12th element is a hyphen.*/
					if(date.length()>14)
					{
						String timezone="GMT+";
						timezone=timezone.concat(date.substring(13,15));
					
						timezone=timezone.concat(":");
						timezone=timezone.concat(date.substring(15,17));
						dateTimeDt.setTimeZone(TimeZone.getTimeZone(timezone));
					}
					else if(date.length()==14)
					{
						if(!isPrecisionSet)
						{
							dateTimeDt.setPrecision(TemporalPrecisionEnum.SECOND);
							isPrecisionSet=true;
						}
						String second=date.substring(12,14);
						int secondInt=Integer.parseInt(second);
						dateTimeDt.setSecond(secondInt);
					}
				}//end if
				else
				{
					//do nothing
					break;
				}
			case 12:
				
				if(!isPrecisionSet)
				{
					dateTimeDt.setPrecision(TemporalPrecisionEnum.MINUTE);
					isPrecisionSet=true;
				}
				String minute=date.substring(10,12);
				int minuteInt=Integer.parseInt(minute);
				dateTimeDt.setMinute(minuteInt);
				
			case 10:
				String hour=date.substring(8,10);
				int hourInt=Integer.parseInt(hour);
				dateTimeDt.setHour(hourInt);
			case 8:
				dayExist=true;
				if(!isPrecisionSet)
				{
					dateTimeDt.setPrecision(TemporalPrecisionEnum.DAY);
					isPrecisionSet=true;
				}
				String day=date.substring(6,8);
				int dayInt=Integer.parseInt(day);
				dateTimeDt.setDay(dayInt);
			case 6:
				monthExist=true;
				if(!isPrecisionSet)
				{
					dateTimeDt.setPrecision(TemporalPrecisionEnum.MONTH);
					isPrecisionSet=true;
				}
				String month=date.substring(4,6);
				int monthInt=Integer.parseInt(month);
				if(!dayExist)
				{
					//System.out.println(monthInt);
					dateTimeDt.setMonth(monthInt);
				}
				else 
					dateTimeDt.setMonth(monthInt-1);
				
			case 4:
				if(!isPrecisionSet)
				{
					dateTimeDt.setPrecision(TemporalPrecisionEnum.YEAR);
					isPrecisionSet=true;
				}
				String year=date.substring(0,4);
				int yearInt=Integer.parseInt(year);
				if(!monthExist)
					dateTimeDt.setYear(yearInt+1);
				else
					dateTimeDt.setYear(yearInt);
		}

		return dateTimeDt;
	}

	// Helper Functions
	private InstantDt dateParserInstant(String date)
	{
		InstantDt instantDt = new InstantDt();
		boolean isPrecisionSet=false;
		boolean dayExist=false;
		boolean monthExist=false;
		switch(date.length())
		{	
			default:
				if(date.length()>12)
				{
					if(!isPrecisionSet)
					{
						instantDt.setPrecision(TemporalPrecisionEnum.MINUTE);
						isPrecisionSet=true;
					}
					/*12th element is a hyphen.*/
					if(date.length()>14)
					{
						String timezone="GMT+";
						timezone=timezone.concat(date.substring(13,15));
					
						timezone=timezone.concat(":");
						timezone=timezone.concat(date.substring(15,17));
						instantDt.setTimeZone(TimeZone.getTimeZone(timezone));
					}
					else if(date.length()==14)
					{
						if(!isPrecisionSet)
						{
							instantDt.setPrecision(TemporalPrecisionEnum.SECOND);
							isPrecisionSet=true;
						}
						String second=date.substring(12,14);
						int secondInt=Integer.parseInt(second);
						instantDt.setSecond(secondInt);
					}
				}//end if
				else
				{
					//do nothing
					break;
				}
			case 12:
				
				if(!isPrecisionSet)
				{
					instantDt.setPrecision(TemporalPrecisionEnum.MINUTE);
					isPrecisionSet=true;
				}
				String minute=date.substring(10,12);
				int minuteInt=Integer.parseInt(minute);
				instantDt.setMinute(minuteInt);
				
			case 10:
				String hour=date.substring(8,10);
				int hourInt=Integer.parseInt(hour);
				instantDt.setHour(hourInt);
			case 8:
				dayExist=true;
				if(!isPrecisionSet)
				{
					instantDt.setPrecision(TemporalPrecisionEnum.DAY);
					isPrecisionSet=true;
				}
				String day=date.substring(6,8);
				int dayInt=Integer.parseInt(day);
				instantDt.setDay(dayInt);
			case 6:
				monthExist=true;
				if(!isPrecisionSet)
				{
					instantDt.setPrecision(TemporalPrecisionEnum.MONTH);
					isPrecisionSet=true;
				}
				String month=date.substring(4,6);
				int monthInt=Integer.parseInt(month);
				if(!dayExist)
				{
					//System.out.println(monthInt);
					instantDt.setMonth(monthInt);
				}
				else 
					instantDt.setMonth(monthInt-1);
				
			case 4:
				if(!isPrecisionSet)
				{
					instantDt.setPrecision(TemporalPrecisionEnum.YEAR);
					isPrecisionSet=true;
				}
				String year=date.substring(0,4);
				int yearInt=Integer.parseInt(year);
				if(!monthExist)
					instantDt.setYear(yearInt+1);
				else
					instantDt.setYear(yearInt);
		}//end switch
		return instantDt;

	}
	
	// Following method is a recursive one and will be used as helper for StructDocText2Narrative
	// Since it calls itself repeatedly and handles with different types of objects, parameter is taken as Object
	// However, parameters of type StrucDocText should be given by the caller
	private String StrucDocText2String( Object param ){
		if( param instanceof org.openhealthtools.mdht.uml.cda.StrucDocText ) {
			org.openhealthtools.mdht.uml.cda.StrucDocText paramStrucDocText = (org.openhealthtools.mdht.uml.cda.StrucDocText)param;
			return "<div>" +StrucDocText2String(  paramStrucDocText.getMixed() ) + "</div>";
		} 
		else if( param instanceof BasicFeatureMap ){
			String returnValue = "";
			for( Object object : (BasicFeatureMap)param ){
				String pieceOfReturn = StrucDocText2String( object );
				if( pieceOfReturn != null && !pieceOfReturn.isEmpty() ){
					returnValue = returnValue + pieceOfReturn;
				}
			}
			return returnValue;
		} 
		else if( param instanceof EStructuralFeatureImpl.SimpleFeatureMapEntry ){
			return ((EStructuralFeatureImpl.SimpleFeatureMapEntry)param).getValue().toString();
		} 
		else if( param instanceof EStructuralFeatureImpl.ContainmentUpdatingFeatureMapEntry){
			EStructuralFeatureImpl.ContainmentUpdatingFeatureMapEntry entry = (EStructuralFeatureImpl.ContainmentUpdatingFeatureMapEntry)param;
			return "<"+entry.getEStructuralFeature().getName()
					+ getAttributeHelperStrucDocText2String(entry)
					+">" + StrucDocText2String( entry.getValue() ) + "</"+entry.getEStructuralFeature().getName()+">";
		} 
		else if( param instanceof org.eclipse.emf.ecore.xml.type.impl.AnyTypeImpl ){
			// since the name and the attributes are taken already, we just send the mixed of anyTypeImpl
			return StrucDocText2String( ((org.eclipse.emf.ecore.xml.type.impl.AnyTypeImpl)param).getMixed() );
		} 
		else{
			// Undesired situtation
			// Check the class of param
			return null;
		}
	}
	
	// Helper for StrucDocText2String
	private String getAttributeHelperStrucDocText2String( EStructuralFeatureImpl.ContainmentUpdatingFeatureMapEntry entry ){
		// This method extracts attributes from AnyTypeImpl
		// Return example: border="1"
		if( entry.getValue() instanceof org.eclipse.emf.ecore.xml.type.impl.AnyTypeImpl ){
			String returnValue = "";
			for( FeatureMap.Entry attribute : ((org.eclipse.emf.ecore.xml.type.impl.AnyTypeImpl) entry.getValue()).getAnyAttribute() ){
				String name = attribute.getEStructuralFeature().getName();
				String value = attribute.getValue().toString();
				if( name != null && !name.isEmpty()){
					// we may have attributes which doesn't have any value
					returnValue = returnValue + " " + name;
					if( value != null && !value.isEmpty() ){
						returnValue = returnValue + "=\""+value+"\"";
					}
				}
			}
			return returnValue;
		} else{
			// Undesired situtation
			// Check the class of entry.getValue()
			return null;
		}
	}
}
