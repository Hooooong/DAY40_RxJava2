Android Programing
----------------------------------------------------
### 2017.11.15 33일차

#### 예제
____________________________________________________

#### 공부정리
____________________________________________________

##### __RxJava__

- RxJava 란?

  > Android 에서 기본적으로 데이터에 대한 동기, 비동기적 처리를 하는데, 이 때 처리해야 할 다양한 콜백이나 오류처리, Thead 처리를 해야 한다. RxJava 는 이러한 동기, 비동기 처리에 대한 콜백, Thread 작업, Observer Pattern 등 개발자가 처리해야 할 다양한 작업들을 간편하게 도와주는 Library 이다.

  - RxJava 는 Observer Pattern 의 기능을 가지고 있다.

  - 기본적으로 Observer Pattern 은 Subject 에 기반을 두고, Subject 에 대한 변경사항이 있으면 자신의 List에 존재하는 Observer 들에게 notify 을 전달하는 구조이다.

  - RxJava 에는 notify(onNext) 외에 onComplete, onError 에 대한 기능이 추가되었다.

- RxJava 설정

  - Gradle

  ```xml
  android {
    // 생략 ....

    /*
    * 람다식을 지원해주는 Complie Options
    * JDK 1.8 허용
    * */
    compileOptions{
        targetCompatibility 1.8
        sourceCompatibility 1.8
    }
  }

  dependencies {
      // 생략 ....
      compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
  }
  ```

- RxJava 사용법

  - Emmitter(발행자) 설정 : `Observable` + `.create(), .from(), .just(), .empty(), .defer() 등등...`

  ```java
  // 1. 데이터 발행 ( Emitter 가 Observable 생성 )
  // 무조건 Sub Thread 에서 돌아간다.
  Observable<T> observable = Observable.create(new ObservableOnSubscribe<T>() {
      @Override
      public void subscribe(ObservableEmitter<T> e) throws Exception {
          try {
              // next 설정(값을 넘겨주는 설정)
              e.onNext(데이터);
              Thread.sleep(1000);
              // onComplete 설정(완료 설정)
              e.onComplete();
          } catch (Exception ex) {
              e.onError(ex);
              throw ex;
          }
      }
  });

  // Lambda 작성
  observable = Observable.create(e -> {
            try {
                // next 설정(값을 넘겨주는 설정)
                e.onNext(데이터);
                Thread.sleep(1000);
                // onComplete 설정(완료 설정)
                e.onComplete();
                // onComplete 설정(완료 설정)
                e.onComplete();
            } catch (Exception ex) {
                e.onError(ex);
                throw ex;
            }
        });
  ```

  - Consumer(소비자) 설정 : `Observable.subscribe()`

  ```java
  observable.subscribe(
                  new Consumer<String>() {
                      @Override
                      public void accept(String str) throws Exception {
                          // onNext() 에 대한 처리
                      }
                  }, new Consumer<Throwable>() {
                      @Override
                      public void accept(Throwable throwable) throws Exception {
                          // onError() 에 대한 처리
                      }
                  }, new Action() {
                      @Override
                      public void run() throws Exception {
                          // onComplete() 에 대한 처리
                      }
                  });

  //lambda 작성
  observable.subscribe(
                str -> {
                  // onNext() 에 대한 처리
                },
                throwable -> {
                  // onError() 에 대한 처리
                },
                () -> {
                  // onComplete() 에 대한 처리
                });
  ```

- RxJava 의 흐름

  ![RxJava](https://github.com/Hooooong/DAY40_RxJava2/blob/master/image/RxJava%ED%9D%90%EB%A6%84%EB%8F%84.png)

  1. Emitter 가 아이템(데이터)을 발행

  2. Consumer 가 아이템(데이터)을 사용

##### __Observable TRANSFORMATION__

- Observable.zip

  - `Observable.zip()` 은 2개의 ObservableSource 들을 합치는 효과를 줄 수 있다.

  - zip 은 이벤트가 Pair 되지 않으면 실행되지 않는다. ( 1번 데이터가 4개이고, 2번 데이터가 3개일 때 )

  ```java
  // Observer.zip 은 2개의 데이터들을 합치는 효과를 줄 수 있음
  Observable<T> observableZip = Observable.zip(
                // Observable 발행1,
                // Observable 발행1,
                new BiFunction<Object, Object, T>() {
                    @Override
                    public T apply(Object o, Object o2) throws Exception {
                        return 반환할 값;
                    }
                }

        );

  // Lambda 작성
  observableZip = Observable.zip(
                  Observable.just("BeWHY", "Curry"),
                  Observable.just("Singer", "Basketball Player"),
                  (item1, item2) -> "jop : ".concat(item1).concat(", Name : ").concat(item2)
          );
  ```

- Observable.concat

  - `Observable.concat()` 은 2개의 ObservableSource 들을 직렬로 합치는 효과를 줄 수 있다.

  ```java
  Observable<String> observableConcat = Observable.concat(
          Observable.just("BeWHY", "Curry"),
          Observable.just("Singer", "Basketball Player"));

  // return ObservableSource
  ```
##### __Observable FILTERING__

- Observable.filter

  - `Observable.filter()` 는 데이터를 구별하기 위해 사용된다.

  - 데이터를 검증하여 일부 데이터만 사용하거나, 사용하지 않을 수 있다.

  ```java
  // Observable객체 생성
  Observable<T> observable = Observer.create(/*생략*/);
  observable.filter(new Predicate<T>() {
                    @Override
                    public boolean test(T str) throws Exception {
                        return 데이터 검증 구역;
                    }
                })
  // Lambda 작성
  observable.filter(str -> 데이터 검증 구역);
  ```

- Observable.map

  - `Observable.map()` 은 데이터를 가공하기 위해 사용된다.

  ```java
  // Observable객체 생성
  Observable<T> observable = Observer.create(/*생략*/);
  observable.map(new Function<T, Object>() {
                    @Override
                    public Object apply(T  s) throws Exception {
                        return 데이터 가공 구역;
                    }
                })
  // Lambda 작성
  observable.map(str -> 데이터 가공 구역)
  ```

- Observable.flatMap

  - `Observable객체.flatMap()` 은 데이터를 여러개로 가공하기 위해 사용된다.

  - 반환값은 `ObservableSource` 이다.

  ```java
  // Observable객체 생성
  Observable<T> observable = Observer.create(/*생략*/);
  observable.flatMap(new Function<T, ObservableSource<?>>() {
              @Override
              public ObservableSource<?> apply(T s) throws Exception {
                  return 데이터를 여러개로 가공하여 생성하는 구역;
              }
          })
  //Lambda 작성
  observable.flatMap(item -> 데이터를 여러개로 가공하여 생성하는 구역)
  ```

- 참조 : [RxJava](https://github.com/Hooooong/DAY40_RxJava2/blob/master/pdf/RxJava.pdf), [RxJava Marbles](http://rxmarbles.com/), [RxJava pdf](https://github.com/Hooooong/DAY40_RxJava2/blob/master/pdf/RxJava.pdf)
