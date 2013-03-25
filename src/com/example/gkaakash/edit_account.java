package com.example.gkaakash;

import java.util.ArrayList;
import java.util.List;
import com.gkaakash.controller.Account;
import com.gkaakash.controller.Preferences;
import com.gkaakash.controller.Startup;
import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class edit_account extends Activity{
	static String accCodeCheckFlag;
    private ListView List;
    private EditText etSearch;
    Spinner sSearchAccountBy;
    private ArrayList<String> array_sort= new ArrayList<String>();
    int textlength=0;
    static Integer client_id;
    private Account account;
    private Object[] accountnames;
    private Object[] accountcodes;
    List getList;
    List accCode_list;
    AlertDialog dialog;
    static Object[] accountDetail;
    ArrayList accountDetailList;
    static int flag = 1;
   
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_acc_tab);
        
        account = new Account();
        client_id = Startup.getClient_id();
        
        List = (ListView) findViewById(R.id.ltAccname);
        List.setCacheColorHint(color.transparent);
        List.setTextFilterEnabled(true);
        
        etSearch = (EditText) findViewById(R.id.etSearch);
        sSearchAccountBy = (Spinner) findViewById(R.id.sSearchAccountBy);
        
        Preferences preferencObj = new Preferences();
  	    // call getPrefernece to get set preference related to account code flag 
  	    accCodeCheckFlag = preferencObj.getPreferences(new Object[]{"2"},client_id);
        
  	    //set visibility of spinner
	  	if (accCodeCheckFlag.equals("automatic")) {
	  		sSearchAccountBy.setVisibility(Spinner.GONE);
	    } else {
	    	sSearchAccountBy.setVisibility(Spinner.VISIBLE);
	    }
        
        //when spinner(search by account name or code) item selected, set the hint in search edittext 
        setOnItemSelectedListener();
        
        //get all acoount names in list view on load
        accountnames = (Object[])account.getAllAccountNames(client_id);
        getResultList(accountnames);
        
        //search account
        searchAccount();
        
        //edit or delete account
        editAccount();
        
 }
    
  private void setOnItemSelectedListener() {
	  sSearchAccountBy.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position,long id) {
				if(position == 0){
					etSearch.setHint("Search by name");
					flag = 1;
					//get all acoount names in list view
					accountnames = (Object[])account.getAllAccountNames(client_id);
			        getResultList(accountnames);
				}
				if(position == 1){
					etSearch.setHint("Search by code");
					flag = 2;
					//get all acoount codes in list view
					accountcodes = (Object[])account.getAllAccountCodes(client_id);
					getResultList(accountcodes);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing!!
				
			}
		});
		
	}

private void editAccount() {
	  List = (ListView) findViewById(R.id.ltAccname);
	  List.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
			
			final CharSequence[] items = { "Edit account", "Delete account" };
			//creating a dialog box for popup
	        AlertDialog.Builder builder = new AlertDialog.Builder(edit_account.this);
	        //setting title
	        builder.setTitle("Edit/Delete Account");
	        //adding items
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface which, int pos) {
	        	if(accCodeCheckFlag.equals("automatic")){
	        		  flag = 1;
	        	  }
	        	  Object[] params1 = new Object[]{List.getItemAtPosition(position).toString(),flag,pos};
	        	  System.out.println(List.getItemAtPosition(position));
	    		  final String accountDeleteValue =  (String) account.deleteAccount(params1,client_id);
	    		  String message = "";
	    		  System.out.println("value"+accountDeleteValue);
	    		  
	    		  if("account deleted".equals(accountDeleteValue)){
	  			  message = "Account '"+List.getItemAtPosition(position).toString()+"' has been deleted successfully";
	  		  }
	    		  else if("account can be edited".equals(accountDeleteValue)){
	  			  message = "edit";
	  		  }
	  		  else if("has both opening balance and trasaction".equals(accountDeleteValue)){
	  			  message = "' has both opening balance and transaction, it can't be";
	  		  }
	  		  else if("has opening balance".equals(accountDeleteValue)){
	  			  message = "' has opening balance, it can't be";
	  		  }
	  		  else if("has transaction".equals(accountDeleteValue)){
	  			  message = "' has transaction, it can't be";
	  		  }
	    		  
	    		  final String msg = message;
	        	
	        	
	        	
	        	//code for the actions to be performed on clicking popup item goes here ...
	            switch (pos) {
	                case 0:
                			{
                				
                			//Toast.makeText(edit_account.this,"Clicked on:"+items[pos],Toast.LENGTH_SHORT).show();
                      		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                      	    View layout = inflater.inflate(R.layout.edit_account, (ViewGroup) findViewById(R.id.layout_root));
                      	    AlertDialog.Builder builder = new AlertDialog.Builder(edit_account.this);
                      	    builder.setView(layout);
                      	    builder.setTitle("Edit account");
                      	    
                      	    //get account details
                      	    String queryParam = List.getItemAtPosition(position).toString();
                      	    if(accCodeCheckFlag.equals("automatic")){
                      	    	//search by account name
                      	    	Object[] params = new Object[]{2,queryParam};
              	    			accountDetail = (Object[]) account.getAccount(params,client_id);
                      	    }
                      	    else if (sSearchAccountBy.getVisibility() == View.VISIBLE) {
                      	    	    // Its visible
                      	    		if(sSearchAccountBy.getSelectedItemPosition()== 0){
                      	    			//search by account name
                      	    			Object[] params = new Object[]{2,queryParam};
                      	    			accountDetail = (Object[]) account.getAccount(params,client_id);
                      	    			
                      	    		}
                      	    		else if(sSearchAccountBy.getSelectedItemPosition()== 1){
                      	    			//search by account code
                      	    			Object[] params = new Object[]{1,queryParam};
                      	    			accountDetail = (Object[]) account.getAccount(params,client_id);
                      	    			 
                      	    		}  
                      	    }
                      	    
                      	    accountDetailList = new ArrayList();
	              	        for(Object ad : accountDetail)
	              	        {
	              	        	Object a = (Object) ad;
	              	        	accountDetailList.add(a.toString());
	              	          
	              	        }
	              	        
	              	      
              	            //account name
              	            final Button bEditAccountName = (Button)layout.findViewById(R.id.bEditAccountName);
              	            final TextView tvEditAccountName = (TextView) layout.findViewById(R.id.tvEditAccountName);
              	            final String oldAccountName = accountDetailList.get(3).toString();
              	            tvEditAccountName.setText(oldAccountName);
              	            final EditText etEditAccountName = (EditText)layout.findViewById(R.id.etEditAccountName);
              	            etEditAccountName.setVisibility(EditText.GONE);
              	            tvEditAccountName.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									tvEditAccountName.setVisibility(TextView.GONE);
									etEditAccountName.setVisibility(EditText.VISIBLE);
									etEditAccountName.setText(oldAccountName);
									bEditAccountName.setVisibility(Button.GONE);
								}
							});
                      	         
	              	        //opening balance
	              	        final Button bEditOpBal = (Button)layout.findViewById(R.id.bEditOpBal);
	            	        final TextView tvEditOpBal = (TextView) layout.findViewById(R.id.tvEditOpBal);
	            	        final String oldOpBal = String.format("%.2f",
	            	        		Float.valueOf(accountDetailList.get(4).toString().trim()).floatValue());
	            	        tvEditOpBal.setText(oldOpBal);
	            	        final EditText etEditOpBal = (EditText)layout.findViewById(R.id.etEditOpBal);
	            	        etEditOpBal.setVisibility(EditText.GONE);
	            	        
	            	        
	            	        
	            	        
	            	        if("Direct Income".equals(accountDetailList.get(1).toString()) 
									|| "Direct Expense".equals(accountDetailList.get(1).toString()) 
									|| "Indirect Income".equals(accountDetailList.get(1).toString()) 
									||  "Indirect Expense".equals(accountDetailList.get(1).toString())){
	            	        	//opening balance is always 0 for above 4 groups, hence set clickable=false
								etEditOpBal.setClickable(false);
								bEditOpBal.setVisibility(Button.GONE);
							}
	            	        else{
	            	        	//set visibility of edittext for editing opening balance
	            	        	tvEditOpBal.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										tvEditOpBal.setVisibility(TextView.GONE);
										etEditOpBal.setVisibility(EditText.VISIBLE);
										etEditOpBal.setText(oldOpBal);
										bEditOpBal.setVisibility(Button.GONE);
									}
		              	        });   
	            	        }
	            	        
	            	        String dialog_button = "Save";
	            	        /*
	            	         * if account has opening balance or transaction or both it can not be deleted
	            	         * so make textview non-editable and edit button invisible
	            	         * and set warning at the top
	            	         */
	            	        if(!"edit".equals(msg)){
	              				tvEditAccountName.setClickable(false);
	              				tvEditOpBal.setClickable(false);
	              				bEditAccountName.setVisibility(Button.GONE);
	              				bEditOpBal.setVisibility(Button.GONE);
	              				dialog_button = "Ok";
	              				TextView tvwarning = (TextView) layout.findViewById(R.id.tvwarning);
	              				tvwarning.setVisibility(TextView.VISIBLE);
	              				tvwarning.setText("Account '"+oldAccountName+msg+" edited.");
	          				}
	            	        
	            	        
              	            //set account code
              	            final TextView tvEditAccountCode = (TextView) layout.findViewById(R.id.tvEditAccountCode);
              	            tvEditAccountCode.setText(accountDetailList.get(0).toString());
              	            
              	            //set group name
              	            final TextView tvEditGroupName = (TextView) layout.findViewById(R.id.tvEditGroupName);
              	            tvEditGroupName.setText(accountDetailList.get(1).toString());
              	            
              	            //set subgroup name
              	            final TextView tvEditSubgroupName = (TextView) layout.findViewById(R.id.tvEditSubgroupName);
              	            tvEditSubgroupName.setText(accountDetailList.get(2).toString());
              	            
                      	            
              	            builder.setPositiveButton(dialog_button, new OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									
									
									
									//get all values
									String newAccountName;
									if(etEditAccountName.getVisibility() == View.VISIBLE){
										newAccountName = etEditAccountName.getText().toString().trim();
									}
									else{
										newAccountName = tvEditAccountName.getText().toString();
									}
									
									String newOpBal;
									if(etEditOpBal.getVisibility() == View.VISIBLE){
										newOpBal = etEditOpBal.getText().toString();
										if(newOpBal.length() < 1){
											newOpBal = "";
										}else
										{
										newOpBal = String.format("%.2f",
				            	        		Float.valueOf(newOpBal.trim()).floatValue());
										}
									} 
									else{ 
										newOpBal = tvEditOpBal.getText().toString();
									}
									String groupname = tvEditGroupName.getText().toString();
									String subgroupname = tvEditSubgroupName.getText().toString();
									String accountcode = tvEditAccountCode.getText().toString();
									
									if((newAccountName.length()<1)&&("".equals(newOpBal)))
									{
										String message = "Please fill field";
										toastValidationMessage(message);
									   
					                }
									else if("".equals(newOpBal))
									{
										String message = "Please fill amount field";
										toastValidationMessage(message);
									}
									else if((newAccountName.length()<1)){
										String message = "Please fill accountname field";
										toastValidationMessage(message);
									}
									if((newAccountName.length()>=1)&&(!"".equals(newOpBal)))
									{ 
										String accountcode_exist = account.checkAccountName(new Object[]{newAccountName,"",""},client_id);
		                                if (!newAccountName.equalsIgnoreCase(oldAccountName)&&accountcode_exist.equals("exist"))
		                                {
		                                	String message = "Account '"+ newAccountName+"' already exist";
											toastValidationMessage(message);
		                                
		                                }else
		                                {
			                        		  
			                        			  Object[] params;
													if("Direct Income".equals(accountDetailList.get(1).toString()) 
															|| "Direct Expense".equals(accountDetailList.get(1).toString()) 
															|| "Indirect Income".equals(accountDetailList.get(1).toString()) 
															||  "Indirect Expense".equals(accountDetailList.get(1).toString())){
														params = new Object[]{newAccountName,accountcode,groupname};
				                      	    			
													} 
													else{
														params = new Object[]{newAccountName,accountcode,groupname,newOpBal};
													}
													account.editAccount(params,client_id);
													
													//set alert messages after account edit
													if(!newAccountName.equalsIgnoreCase(oldAccountName) &&
															!newOpBal.equals(oldOpBal)){
														
														String message = "Account name has been changed from '"+
																oldAccountName+"' to '"+ newAccountName+
																"' and opening balance has been changed from '"+ 
																oldOpBal + "' to '"+ newOpBal+"'";
														toastValidationMessage(message);
													}
													else if(!newAccountName.equalsIgnoreCase(oldAccountName)){
														String message = "Account name has been changed from '"+
																oldAccountName+"' to '"+ newAccountName+"'";
														toastValidationMessage(message);
													}
													else if(!newOpBal.equals(oldOpBal)){
														String message = "Opening balance has been changed from '"+
																oldOpBal+"' to '"+ newOpBal+"'";
														toastValidationMessage(message);
													}
													else{
														if("edit".equals(msg)){
															String message = "No changes made";
															toastValidationMessage(message);
								          				}
													}
													
													setaccountlist();
			                        			  
		                                }
											
									}
									
								}//end of onclick
							});// end of onclickListener
              	             
              	            builder.setNegativeButton("Cancel", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							});
                      	            
              	            dialog=builder.create();
              	           ((Dialog) dialog).show();
              	              WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
              	            //customizing the width and location of the dialog on screen 
              	            lp.copyFrom(dialog.getWindow().getAttributes());
              	            lp.height = 600;
              	            lp.width = 400;
              	            dialog.getWindow().setAttributes(lp);						
	                      }
                		break;
	                case 1:
	                              {
	                            	  
	                        		  System.out.println("value"+accountDeleteValue);
	                        		  if("account deleted".equals(accountDeleteValue)){
	                        			  toastValidationMessage(msg);
	                        			  setaccountlist();
	                        		  }
	                        		  else {
	                          			  toastValidationMessage("Account '"+List.getItemAtPosition(position).toString()+msg+" deleted.");
	                        		  }
	                      }break;
	            }
	        }
	        });
	        //building a complete dialog
			dialog=builder.create();
			dialog.show();
			
			
			
		}
	});
		
	}

	//search account
    private void searchAccount() {
    	//attaching listener to textView
        etSearch.addTextChangedListener(new TextWatcher()
        {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after)
	        {
	        // Abstract Method of TextWatcher Interface.
	        }
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
		        //for loop for search
		        textlength = etSearch.getText().length();
		        array_sort.clear();
		       
		        for (Object acc : getList)
		        {
		            if (textlength <= acc.toString().length())
		            {
		                if(etSearch.getText().toString().equalsIgnoreCase((String) ((String) acc).subSequence(0,textlength)))
		                {
		                    array_sort.add((String)acc);
		                }
		            }
		        }
		       
		        List.setAdapter(new ArrayAdapter<String>(edit_account.this,android.R.layout.simple_list_item_1, array_sort));
	        }
	        @Override
	        public void afterTextChanged(Editable arg0) {
	            // Abstract Method of ArrayAdapter Interface
	        }
        });
		
	}//end of search account by name
    
    
	//get all acoount names or account codes depending upon parameter
    void getResultList(Object[] param){
        getList = new ArrayList();
        for(Object an : param)
        {   
            getList.add(an); //acc_names
        }   
         List.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getList));
       
    }
    
    
    void setaccountlist(){
    	if(accCodeCheckFlag.equals("automatic")){
			//get all updated acoount names in list view
			accountnames = (Object[])account.getAllAccountNames(client_id);
	        getResultList(accountnames);
  	    }
  	    else if (sSearchAccountBy.getVisibility() == View.VISIBLE) {
  	    	    // Its visible
  	    		if(sSearchAccountBy.getSelectedItemPosition()== 0){
  	    			//for search by account name, get all updated acoount names in list view
  	    			accountnames = (Object[])account.getAllAccountNames(client_id);
			        getResultList(accountnames);
  	    			
  	    		}
  	    		else if(sSearchAccountBy.getSelectedItemPosition()== 1){
  	    			//search by account code
  	    			accountnames = (Object[])account.getAllAccountCodes(client_id);
			        getResultList(accountnames);
  	    			 
  	    		}  
  	    }
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     *  to execute code when tab is changed because 
     *  when the tab is clicked onResume is called for that activity
     */
    @Override
    protected void onResume() {
    	super.onResume();
    	//get all acoount names in list view on load
        accountnames = (Object[])account.getAllAccountNames(client_id);
        getResultList(accountnames);
        setaccountlist();
    }
    
    
    /*
     * call this method for alert messages
     * input: a message Strig to be display on alert
     */
    public void toastValidationMessage(String message) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(edit_account.this);
	    builder.setMessage(message)
	           .setCancelable(false)
               .setPositiveButton("Ok",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                           	
                           }
                       });
	               
	     AlertDialog alert = builder.create();
	     alert.show();
		
	} 
}