package au.com.infotrak.infotrakmobile.WRESScreens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import au.com.infotrak.infotrakmobile.InfoTrakApplication;
import au.com.infotrak.infotrakmobile.R;
import au.com.infotrak.infotrakmobile.adapters.WRES.WRESSpinnerAdapter;
import au.com.infotrak.infotrakmobile.business.WRES.CallServiceAPIJSON;
import au.com.infotrak.infotrakmobile.business.WRES.OnCallAPIListener;
import au.com.infotrak.infotrakmobile.business.WRES.WRESUtilities;
import au.com.infotrak.infotrakmobile.datastorage.WRES.WRESDataContext;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESComponent;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESEquipment;
import au.com.infotrak.infotrakmobile.entityclasses.WRES.WRESNewChain;

public class WRESCreateNewChainDetailActivity extends android.support.v7.app.AppCompatActivity {

    private WRESUtilities _utilities = new WRESUtilities(null);
    private ViewClass _viewModel = new ViewClass();
    private WRESDataContext _dbModel = new WRESDataContext(null);

    private long _inspectionId = 0;
    private int _makeAuto = 0;
    private int _modelAuto = 0;

    // LINK
    private long _linkCompartIdAuto = 0;
    private String _linkCompartId = "";
    private String _linkCompart = "";
    private long _linkBrand = 0;
    private String _linkTool = "";
    private String _linkMethod = "";

    // BUSHING
    private long _bushingCompartIdAuto = 0;
    private String _bushingCompartId = "";
    private String _bushingCompart = "";
    private long _bushingBrand = 0;
    private String _bushingTool = "";
    private String _bushingMethod = "";

    // SHOE
    private long _shoeCompartIdAuto = 0;
    private String _shoeCompartId = "";
    private String _shoeCompart = "";
    private long _shoeBrand = 0;
    private long _shoeSize = 0;
    private String _shoeGrouser = "";
    private String _shoeTool = "";
    private String _shoeMethod = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wres_create_new_chain_detail_activity);
        _utilities = new WRESUtilities(this);
        _dbModel = new WRESDataContext(this);

        // Get passing data
        Bundle data = getIntent().getExtras();
        _inspectionId = data.getLong("inspectionId");
        _makeAuto = data.getInt("makeAuto");
        _modelAuto = data.getInt("modelAuto");

        // Action Bar
        _viewModel.customizeActionBar();

        // Select saved data
        ArrayList<WRESComponent> components =_dbModel.selectComponentByInspectionId(_inspectionId);
        WRESComponent linkComponent = getComponent(components, "Link");
        WRESComponent bushingComponent = getComponent(components, "Bushing");
        WRESComponent shoeComponent = getComponent(components, "Shoe");

        // Link Component Type
        ArrayList<WRESNewChain.ComponentType> listLinkComponentType = _dbModel.selectLinkComponentType(_makeAuto, _modelAuto);
        _viewModel.showLinkComponent(listLinkComponentType, linkComponent);

        // Link Brand
        ArrayList<WRESNewChain.Brand> listLinkBrand = _dbModel.selectLinkBrand(_makeAuto);
        _viewModel.showLinkBrand(listLinkBrand, linkComponent);

        // Bushing Component Type
        ArrayList<WRESNewChain.ComponentType> listBushingComponentType = _dbModel.selectBushingComponentType(_makeAuto, _modelAuto);
        _viewModel.showBushingComponent(listBushingComponentType, bushingComponent);

        // Bushing Brand
        ArrayList<WRESNewChain.Brand> listBushingBrand = _dbModel.selectBushingBrand(_makeAuto);
        _viewModel.showBushingBrand(listBushingBrand, bushingComponent);

        // Shoe Component Type
        ArrayList<WRESNewChain.ComponentType> listShoeComponentType = _dbModel.selectShoeComponentType(_makeAuto, _modelAuto);
        _viewModel.showShoeComponent(listShoeComponentType, shoeComponent);

        // Shoe Brand
        ArrayList<WRESNewChain.Brand> listShoeBrand = _dbModel.selectShoeBrand(_makeAuto);
        _viewModel.showShoeBrand(listShoeBrand, shoeComponent);

        // Shoe size
        ArrayList<WRESNewChain.ShoeSize> listShoeSize = _dbModel.selectShoeSize();
        _viewModel.showShoeSize(listShoeSize, shoeComponent);

        // Shoe grouser
        _viewModel.showShoeGrouser(shoeComponent);
    }

    private WRESComponent getComponent(ArrayList<WRESComponent> components, String compart) {

        for (int i = 0; i < components.size(); i++) {
            WRESComponent component = components.get(i);
            String compartCheck = component.get_comparttype();
            if (compart.equals(compartCheck)) {
                return component;
            }
        }

        return null;
    }

    private void saveDB() {

        // Save Link
        if (validateFields(
                _linkCompartIdAuto,
                _linkCompartId,
                _linkCompart,
                _linkBrand,
                _linkTool,
                _linkMethod
        )) {
            EditText linkBudgetLife = (EditText) findViewById(R.id.link_budget_life);
            EditText linkHrsOnSurface = (EditText) findViewById(R.id.link_hrs_on_surface);
            EditText linkCost = (EditText) findViewById(R.id.link_cost);
            _dbModel.saveNewChainComponent(
                    _inspectionId,
                    _utilities.COMPARTTYPE_LINK,
                    "Link",
                    _linkCompartIdAuto, // compartid_auto
                    _linkCompartId,     // compartid
                    _linkCompart,
                    _linkBrand,
                    _linkTool,
                    _linkMethod,
                    linkBudgetLife.getText().toString(),
                    linkHrsOnSurface.getText().toString(),
                    linkCost.getText().toString(),
                    0,""
            );
        }

        // Save Bushing
        if (validateFields(
                _bushingCompartIdAuto,
                _bushingCompartId,
                _bushingCompart,
                _bushingBrand,
                _bushingTool,
                _bushingMethod
        )) {
            EditText bushingBudgetLife = (EditText) findViewById(R.id.bushing_budget_life);
            EditText bushingHrsOnSurface = (EditText) findViewById(R.id.bushing_hrs_on_surface);
            EditText bushingCost = (EditText) findViewById(R.id.bushing_cost);
            _dbModel.saveNewChainComponent(
                    _inspectionId,
                    _utilities.COMPARTTYPE_BUSHING,
                    "Bushing",
                    _bushingCompartIdAuto, // compartid_auto
                    _bushingCompartId,     // compartid
                    _bushingCompart,
                    _bushingBrand,
                    _linkTool,
                    _linkMethod,
                    bushingBudgetLife.getText().toString(),
                    bushingHrsOnSurface.getText().toString(),
                    bushingCost.getText().toString(),
                    0,""
            );
        }

        // Save Shoe
        if (validateFields(
                _shoeCompartIdAuto,
                _shoeCompartId,
                _shoeCompart,
                _shoeBrand,
                _shoeTool,
                _shoeMethod
        )) {
            EditText shoeBudgetLife = (EditText) findViewById(R.id.shoe_budget_life);
            EditText shoeHrsOnSurface = (EditText) findViewById(R.id.shoe_hrs_on_surface);
            EditText shoeCost = (EditText) findViewById(R.id.shoe_cost);
            _dbModel.saveNewChainComponent(
                    _inspectionId,
                    _utilities.COMPARTTYPE_SHOE,
                    "Shoe",
                    _shoeCompartIdAuto, // compartid_auto
                    _shoeCompartId,     // compartid
                    _shoeCompart,
                    _shoeBrand,
                    _shoeTool,
                    _shoeMethod,
                    shoeBudgetLife.getText().toString(),
                    shoeHrsOnSurface.getText().toString(),
                    shoeCost.getText().toString(),
                    _shoeSize,
                    _shoeGrouser
            );
        }
    }

    private Boolean validateFields(
        long CompartIdAuto,
        String CompartId,
        String Compart,
        long Brand,
        String Tool,
        String Method
    ) {
        
        if (CompartIdAuto == 0
            || !_utilities.validateString(CompartId)
            || !_utilities.validateString(Compart)
            || Brand == 0
            || !_utilities.validateString(Tool)
            || !_utilities.validateString(Method))
            return false;

        return true;
    }

    public void launchPreviousScreen(View view) {

        // UPDATE DB
        saveDB();

        // Start new Activity
        Intent intent = new Intent(this,WRESCreateNewChainActivity.class);
        intent.putExtra("inspectionId", _inspectionId);
        startActivity(intent);
        this.finish();
    }

    public void saveNewChainComponent(View view) {

        // Update DB
        saveDB();

        // Download limits for new chain basing on compartid_auto
        _viewModel.SetProgressBar();
        GetAndSaveLimits();
    }

    private void navigateScreen() {
        // Pass data
        WRESEquipment equipment = _dbModel.selectInspectionById(_inspectionId);
        Intent intent = new Intent(this,WRESInitialDetailsActivity.class);
        intent.putExtra("equipment", equipment);
        startActivity(intent);
        this.finish();
    }

    private void GetAndSaveLimits()
    {
        String apiUrl = ((InfoTrakApplication)this.getApplication()).getServiceUrl();
        String apiGetLimits = apiUrl + _utilities.api_get_limits_by_compartid_auto;
        CallServiceAPIJSON getRequest = new CallServiceAPIJSON(
                getApplicationContext(), new OnCallAPIListener<String>() {
            @Override
            public void onSuccess(String result) {
                _viewModel.HideProgressBar();
                navigateScreen();
            }

            @Override
            public void onFailure(Exception e) {
                _viewModel.HideProgressBar();
                Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                navigateScreen();
            }
        });

        try {
            getRequest.execute(apiGetLimits + "?compartIdAutoList="
                    + _linkCompartIdAuto + ","
                    + _bushingCompartIdAuto + ","
                    + _shoeCompartIdAuto
                    , "Limits").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////
    // View class
    private class ViewClass
    {
        private ProgressDialog _progressDialog;

        public void customizeActionBar()
        {
            // Customize action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EB5757")));
        }

        public void showLinkComponent(ArrayList<WRESNewChain.ComponentType> componentTypeList, WRESComponent component)
        {

            // Show
            final Spinner spinnerCondition = (Spinner) findViewById(R.id.link_component_type_spinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    WRESCreateNewChainDetailActivity.this,
                    R.layout.wres_spinner_layout,
                    componentTypeList);

            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            WRESCreateNewChainDetailActivity.this));

            // Listener
            spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    WRESNewChain.ComponentType selectedItem =
                            (WRESNewChain.ComponentType) parentView.getItemAtPosition(position);
                    if (selectedItem != null) {
                        _linkCompartIdAuto = selectedItem.getCompartid_auto();
                        _linkCompartId = selectedItem.getCompartid();
                        _linkCompart = selectedItem.getCompart();
                        _linkTool = selectedItem.getDefaultTool();
                        _linkMethod = selectedItem.getMethod();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // Set spinner value
            if (component == null) return;
            long selected = component.get_compartid_auto();
            if (selected > 0)
            {
                int spinnerPosition = -1;
                for (int i = 0; i < componentTypeList.size(); i++)
                {
                    if (componentTypeList.get(i).getCompartid_auto() == selected) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);
            }
        }

        public void showLinkBrand(ArrayList<WRESNewChain.Brand> list, WRESComponent component)
        {
            // Show
            final Spinner spinnerCondition = (Spinner) findViewById(R.id.link_brand_spinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    WRESCreateNewChainDetailActivity.this,
                    R.layout.wres_spinner_layout,
                    list);

            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            WRESCreateNewChainDetailActivity.this));

            // Listener
            spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    WRESNewChain.Brand selectedItem =
                            (WRESNewChain.Brand) parentView.getItemAtPosition(position);
                    if (selectedItem != null)
                        _linkBrand = selectedItem.getMake_auto();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // Set spinner value
            if (component == null) return;
            long selected = component.get_brand_auto();
            if (selected > 0)
            {
                int spinnerPosition = -1;
                for (int i = 0; i < list.size(); i++)
                {
                    if (list.get(i).getMake_auto() == selected) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);
            }
        }

        public void showBushingComponent(ArrayList<WRESNewChain.ComponentType> componentTypeList, WRESComponent component)
        {

            // Show
            final Spinner spinnerCondition = (Spinner) findViewById(R.id.bushing_component_type_spinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    WRESCreateNewChainDetailActivity.this,
                    R.layout.wres_spinner_layout,
                    componentTypeList);

            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            WRESCreateNewChainDetailActivity.this));
            // Listener
            spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    WRESNewChain.ComponentType selectedItem =
                            (WRESNewChain.ComponentType) parentView.getItemAtPosition(position);
                    if (selectedItem != null) {
                        _bushingCompartIdAuto = selectedItem.getCompartid_auto();
                        _bushingCompartId = selectedItem.getCompartid();
                        _bushingCompart = selectedItem.getCompart();
                        _bushingTool = selectedItem.getDefaultTool();
                        _bushingMethod = selectedItem.getMethod();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // Set spinner value
            if (component == null) return;
            long selected = component.get_compartid_auto();
            if (selected > 0)
            {
                int spinnerPosition = -1;
                for (int i = 0; i < componentTypeList.size(); i++)
                {
                    if (componentTypeList.get(i).getCompartid_auto() == selected) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);
            }
        }

        public void showBushingBrand(ArrayList<WRESNewChain.Brand> list, WRESComponent component)
        {
            // Show
            final Spinner spinnerCondition = (Spinner) findViewById(R.id.bushing_brand_spinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    WRESCreateNewChainDetailActivity.this,
                    R.layout.wres_spinner_layout,
                    list);

            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            WRESCreateNewChainDetailActivity.this));

            // Listener
            spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    WRESNewChain.Brand selectedItem =
                            (WRESNewChain.Brand) parentView.getItemAtPosition(position);
                    if (selectedItem != null)
                        _bushingBrand = selectedItem.getMake_auto();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // Set spinner value
            if (component == null) return;
            long selected = component.get_brand_auto();
            if (selected > 0)
            {
                int spinnerPosition = -1;
                for (int i = 0; i < list.size(); i++)
                {
                    if (list.get(i).getMake_auto() == selected) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);
            }
        }

        public void showShoeComponent(ArrayList<WRESNewChain.ComponentType> componentTypeList, WRESComponent component)
        {

            // Show
            final Spinner spinnerCondition = (Spinner) findViewById(R.id.shoe_component_type_spinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    WRESCreateNewChainDetailActivity.this,
                    R.layout.wres_spinner_layout,
                    componentTypeList);

            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            WRESCreateNewChainDetailActivity.this));

            // Listener
            spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    WRESNewChain.ComponentType selectedItem =
                            (WRESNewChain.ComponentType) parentView.getItemAtPosition(position);
                    if (selectedItem != null) {
                        _shoeCompartIdAuto = selectedItem.getCompartid_auto();
                        _shoeCompartId = selectedItem.getCompartid();
                        _shoeCompart = selectedItem.getCompart();
                        _shoeTool = selectedItem.getDefaultTool();
                        _shoeMethod = selectedItem.getMethod();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // Set spinner value
            if (component == null) return;
            long selected = component.get_compartid_auto();
            if (selected > 0)
            {
                int spinnerPosition = -1;
                for (int i = 0; i < componentTypeList.size(); i++)
                {
                    if (componentTypeList.get(i).getCompartid_auto() == selected) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);
            }
        }

        public void showShoeBrand(ArrayList<WRESNewChain.Brand> list, WRESComponent component)
        {
            // Show
            final Spinner spinnerCondition = (Spinner) findViewById(R.id.shoe_brand_spinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    WRESCreateNewChainDetailActivity.this,
                    R.layout.wres_spinner_layout,
                    list);

            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            WRESCreateNewChainDetailActivity.this));

            // Listener
            spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    WRESNewChain.Brand selectedItem =
                            (WRESNewChain.Brand) parentView.getItemAtPosition(position);
                    if (selectedItem != null)
                        _shoeBrand = selectedItem.getMake_auto();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // Set spinner value
            if (component == null) return;
            long selected = component.get_brand_auto();
            if (selected > 0)
            {
                int spinnerPosition = -1;
                for (int i = 0; i < list.size(); i++)
                {
                    if (list.get(i).getMake_auto() == selected) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);
            }
        }

        public void showShoeSize(ArrayList<WRESNewChain.ShoeSize> list, WRESComponent component)
        {
            // Show
            final Spinner spinnerCondition = (Spinner) findViewById(R.id.shoe_size_spinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    WRESCreateNewChainDetailActivity.this,
                    R.layout.wres_spinner_layout,
                    list);

            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            WRESCreateNewChainDetailActivity.this));

            // Listener
            spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    WRESNewChain.ShoeSize selectedItem =
                            (WRESNewChain.ShoeSize) parentView.getItemAtPosition(position);
                    if (selectedItem != null)
                        _shoeSize = selectedItem.getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // Set spinner value
            if (component == null) return;
            long selected = component.get_shoe_size_id();
            if (selected > 0)
            {
                int spinnerPosition = -1;
                for (int i = 0; i < list.size(); i++)
                {
                    if (list.get(i).getId() == selected) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);
            }
        }

        public void showShoeGrouser(WRESComponent component)
        {
            // Show
            String[] list = _utilities.shoe_grouser;
            final Spinner spinnerCondition = (Spinner) findViewById(R.id.shoe_grouser_spinner);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(
                    WRESCreateNewChainDetailActivity.this,
                    R.layout.wres_spinner_layout,
                    list);

            spinnerCondition.setAdapter(
                    new WRESSpinnerAdapter(
                            spinnerArrayAdapter,
                            R.layout.wres_spinner_row_nothing_selected,
                            R.layout.wres_spinner_row_something_selected,
                            WRESCreateNewChainDetailActivity.this));

            // Listener
            spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedItem =
                            (String) parentView.getItemAtPosition(position);
                    if (selectedItem != null)
                        _shoeGrouser = selectedItem;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });

            // Set spinner value
            if (component == null) return;
            String selected = component.get_grouser();
            if (_utilities.validateString(selected))
            {
                int spinnerPosition = -1;
                for (int i = 0; i < list.length; i++)
                {
                    if (list[i].equals(selected)) {
                        spinnerPosition = i + 1;
                        break;
                    }
                }

                if (spinnerPosition != -1)
                    spinnerCondition.setSelection(spinnerPosition);
            }
        }

        /////////////////
        // Progress bar
        public void SetProgressBar() {
            _progressDialog = new ProgressDialog(WRESCreateNewChainDetailActivity.this);
            _progressDialog.setMessage(WRESCreateNewChainDetailActivity.this.getString(R.string.text_data_loading));
            _progressDialog.show();
        }

        public void HideProgressBar() {
            _progressDialog.dismiss();
        }
    }
}
