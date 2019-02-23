package com.example.albert.ccumis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class DepartmentSearchDialogFragment extends DialogFragment {
  Callback callback;
  EditText editText;
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    getDialog().setCanceledOnTouchOutside(true);

    View view = inflater.inflate(R.layout.dialog_search_department, container, false);
    ListView listView = view.findViewById(R.id.spinnerList);
    final DepartmentAdapter adapter = new DepartmentAdapter(getContext(), android.R.layout.simple_list_item_1);
    new Thread(new Runnable() {
      @Override
      public void run() {
        final ArrayList<Department> departments = getDepartments();
        if(departments != null) {
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              adapter.setDepartments(departments);
            }
          });
        }
      }
    }).start();
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        callback.onSelect(adapter.getDepartment(position));
      }
    });

    editText = view.findViewById(R.id.departmentSearchText);


    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapter.getFilter().filter(s.toString());
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    Window window = getDialog().getWindow();
    window.setLayout(600 , 800);
    window.setGravity(Gravity.CENTER);
  }

  private ArrayList<Department> getDepartments() {
    try {

      ArrayList<Department> departments = new ArrayList<>();

      Connection.Response response = Jsoup.connect("http://mis.cc.ccu.edu.tw/parttime/main2.php").execute();
      Document document = response.parse();
      boolean flag = true;
      if(document.title().equals("學習暨勞僱時數登錄系統")) {
        Elements options = document.selectFirst("select").select("option");
        for(Element option : options) {
          Department department = new Department();
          department.name = option.text();
          department.value = option.val();
          departments.add(department);
        }
        return departments;
      } else {
        // Login fail
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      // Connection error
      return null;
    }
  }

  // Set activity callback
  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public interface Callback {
    void onCancelled();
    void onSelect(Department department);
  }
}
