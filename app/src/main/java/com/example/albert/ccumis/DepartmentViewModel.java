package com.example.albert.ccumis;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class DepartmentViewModel extends AndroidViewModel {
  private DepartmentRepository repository;
  public DepartmentViewModel(@NonNull Application application) {
    super(application);
    repository = new DepartmentRepository(application);
  }

  public LiveData<List<Department>> getAll(int type) {
    return repository.getAll(type);
  }

  public void nuke(int type) {
    repository.nuke(type);
  }
}