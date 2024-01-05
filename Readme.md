This is second in series of projects looking at Coroutines in Android.

Previous project was focussed on Blocking nature of threads, Suspension and how 
Coroutines use Suspension to work more efficiently compared to stand-alone Threads.

In this project the focus is on Coroutine structure, how the components behave and 
work with each other. 

CoroutineScope -
Each Coroutine runs within a scope. A scope defines the lifecycle tied to the Coroutine. For some 
Android components like Activity, have their own lifecycleScope or a ViewModelScope.

A scope determines when the coroutine will cancel execution. Generally it will be when the component's 
onStop or onClear callback is called. Or we can also define our own CoroutineScope and pass a 
CoroutineContext instance to it. But then we need to explicitly call its cancel function when we 
don't want the coroutine to execute anymore. 

CoroutineContext -
CoroutineContext is an indexed set of Elements. Coroutine context is immutable, but you can add 
elements to a context using plus operator, just like you add elements to a set, producing a new 
context instance. 

Coroutine builders -
Within the Coroutine Scope we can start a Coroutine using one of the Coroutine Builders -
launch, async-await, runBlocking

CoroutineDispatchers -
Dispatchers.Default, Dispatchers.IO, Dispatchers.Unconfirmed
Coroutine Dispatchers are responsible to dispatch the task to one of the threads of the threadpool. 
CoroutineDispatchers are also responsible to switch between threads if any of them becomes suspended. 
This ensures efficient use of CPU. 

CoroutineSchedulars -
Threadpool which is setup, so that CoroutineDispatchers can use to dispatch tasks to. CoroutineSchedular 
is setup when we use one of the Dispatchers when starting a Coroutine. The thread pool has default 
no. of threads equal to number of CPU cores. This is used for Dispatchers.Default. The same threadpool, 
can be extended lazily to include more threads for IO Blocking threads. The max number of threads 
in the threadpool can be 64 or max CPU cores, which ever is larger. 
