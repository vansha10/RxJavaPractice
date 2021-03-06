package com.vansha10.rxjavapractice;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.jakewharton.rxbinding3.view.RxView;
import com.vansha10.rxjavapractice.model.Task;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bufferOperatorUseCase();
    }

    private void createOperator() {
        Observable<Task> taskObservable = Observable
                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(ObservableEmitter<Task> emitter) throws Exception {
                        // Inside the subscribe method iterate through the list of tasks and call onNext(task)
                        for (Task task : DataSource.createTasksList()) {
                            if (!emitter.isDisposed()) {
                                emitter.onNext(task);
                            }
                        }
                        // Once the loop is complete, call the onComplete() method
                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void intervalOperator() {
        Observable<Long> longObservable = Observable
                .interval(1, TimeUnit.SECONDS)
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        // repeat till 5 seconds have passed
                        return aLong <= 5;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        longObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "onNext: " + aLong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void timerOperator() {
        Observable<Long> longObservable = Observable
                .timer(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        longObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(Long aLong) {
                Log.d(TAG, "onNext: " + aLong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void fromArrayOperator() {
        Task[] tasks = new Task[3];
        tasks[0] = new Task("Task 1", false, 1);
        tasks[1] = new Task("Task 2", false, 2);
        tasks[2] = new Task("Task 3", false, 3);
        Observable<Task> taskObservable = Observable
                .fromArray(tasks)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
                disposables.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    private void fromIterableOperator() {
        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .subscribeOn(Schedulers.io())
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        // doing task on background thread doesn't freeze ui
                        Log.d(TAG, "onNext: " + Thread.currentThread().getName());
                        return task.isComplete();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
                disposables.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
    }

    private void fromPublisherOperator() {
        // https://codingwithmitch.com/courses/rxjava-rxandroid-for-beginners/rx-operators-from-publisher/

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.makeQuery().observe(this, new androidx.lifecycle.Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                Log.d(TAG, "onChanged: this is a live data response!");
                try {
                    Log.d(TAG, "onChanged: " + responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void filterOperator() {
        Observable<Task> taskObservable = Observable.
                fromIterable(DataSource.createTasksList())
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        return task.isComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void distinctOperator() {
        // returns list of unique objects

        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createDuplicateTasksList())
                .distinct(new Function<Task, String>() {
                    @Override
                    public String apply(Task task) throws Exception {
                        return task.getDescription(); // the field to test for uniqueness
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void takeWhileOperator() {
        // equivalent of a while loop

        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .takeWhile(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Exception {
                        return task.isComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void mapOperator() {
        Observable<String> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .map(new Function<Task, String>() {
                    @Override
                    public String apply(Task task) throws Exception {
                        return task.getDescription();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void bufferOperator() {
        // simple example

        // emits 2 objects at a time
        Observable<List<Task>> taskObservable = Observable
                .fromIterable(DataSource.createTasksList())
                .buffer(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<List<Task>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(List<Task> tasks) {
                Log.d(TAG, "onNext: Next Batch of tasks............................");
                for (Task task : tasks) {
                    Log.d(TAG, "onNext: " + task.getDescription());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void bufferOperatorUseCase() {
        // use case for tracking ui interactions, counts number of clicks in 4 seconds
        // https://codingwithmitch.com/courses/rxjava-rxandroid-for-beginners/rxjava-operators-buffer/

        // rx binding library converts view clicks to observables
        RxView.clicks(findViewById(R.id.button))
                .map(new Function<Unit, Integer>() {
                    @Override
                    public Integer apply(Unit unit) throws Exception {
                        return 1;
                    }
                })
                .buffer(4, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.d(TAG, "onNext: You clicked the button " + integers.size() + " times!");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // clears all disposables which means rx observers are not observed
        disposables.clear();
    }
}
