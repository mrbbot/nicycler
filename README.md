# Nicycler
Nicycler is a nicer version of Android's `RecyclerView`.

## Installation
Add the library as a Gradle dependency to your module-level `build.gradle`.
```gradle
dependencies {
    compile 'com.mrbbot:nicycler:1.0'
}
```

## Usage
Nicycler works by specifying the data type to contain all the data for a record and the view type to display the record in. For the following example, we'll define a data class called `Text` with a String field called `message` and use a `TextView` to display the text.
```java
static class Text implements Serializable {
    String message;

    Text(String message) {
        this.message = message;
    }
}
```
> Note: this class needs to implement `Serializable` and be in its own file or a static class in order for it to be properly stored when the Activity is paused.

### View
Then, add a `NicyclerView` to your layout file:
```xml
<com.mrbbot.nicycler.NicyclerView
    android:id="@+id/nicycler"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

</com.mrbbot.nicycler.NicyclerView>
```
...and find the view in your activity/fragment:
```java
NicyclerView<Text, TextView> view = findViewById(R.id.nicycler);
```
Alternatively you could just create the Nicycler in your `onCreate` method:
```java
NicyclerView<Text, TextView> view = new NicyclerView<>(this);
```

### Initialisation
The primary logic of the Nicycler is defined in its adapter which should then be used to initialise it:
```java
NicyclerAdapter<Text, TextView> adapter = new NicyclerAdapter<Text, TextView>() {
    @Override
    public TextView onCreate(ViewGroup parent) {
        TextView view = new TextView(parent.getContext());
        view.setPadding(64, 64, 64, 64);
        return view;
    }

    @Override
    public void onBind(TextView view, Text data) {
        view.setText(data.message);
    }
};

view.init(adapter);
```

### Adding/Setting Data
In order to add data to the Nicycler, call the add method:
```java
view.add(new Text("Hello!"));
```
You can add multiple items in a single method call:
```java
view.add(new Text("a"), new Text("b"), new Text("c"));
```
You can also set the data directly, which will remove all of the existing items:
```java
view.set(new Text("x"), new Text("y"), new Text("z"));
```

### Removing Data
Removing data works differently:
```java
view.remove(new Filter<Text>() {
    @Override
    public boolean accept(Text text) {
        return text.message.equals("Hello!");
    }
});
```
If the text matches the filter, the item will be removed. This can be simplified using a lambda expression:
```java
view.remove(text -> text.message.equals("Hello!"));
```

### Updating Data
Updating data works in a similar way to removing data:
```java
view.update(new Filter<Text>() {
    @Override
    public boolean accept(Text text) {
        if(text.message.equals("Hello!")) {
            text.message = "Hi";
            return true;
        }
        return false;
    }
});
```
The filter should return `true` if the item was updated and `false` if it wasn't.

### Saving and Restoring State
To ensure the dataset remains the same when the activity is paused and resumed, or if it is rotated, the state of the Nicycler needs to be saved and restored. Two methods need to be overridden in the activity:
```java
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    view.save(outState);
}

@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    view.restore(savedInstanceState);
}
```

### Swiping
All of the options for a swipe are stored in a `NicyclerSwipe` object. An icon resource ID and a colour for the swipe needs to be defined:
```java
NicyclerSwipe<Text> completeSwipe = new NicyclerSwipe<Text>(R.drawable.ic_check, "#4CAF50") {
    @Override
    public void swipe(Text text, Callback callback) {
        view.remove(t -> t.equals(text));
        callback.callback();
    }
};
```
The callback should be called when you want the item to be updated (i.e. returned to its normal position). By default, the item will always update when it's swiped. This behaviour can be defined by specifying an additional boolean parameter in the constructor:
```java
NicyclerSwipe<Text> completeSwipe = new NicyclerSwipe<Text>(R.drawable.ic_check, "#4CAF50", false) {
	...
}
```
There is also a `canSwipe` method the can be overridden that determines whether an item can be swiped.
```java
NicyclerSwipe<Text> completeSwipe = new NicyclerSwipe<Text>(R.drawable.ic_check, "#4CAF50") {
    @Override
    public boolean canSwipe(Text text) {
        return text.message.toLowerCase().contains("H");
    }

    @Override
    public void swipe(Text text, Callback callback) {
        view.remove(t -> t.equals(text));
        callback.callback();
    }
};
```

Once we've created the `NicyclerSwipe` object, we can pass it to the `init` function as we did for the adapter.
```java
view.init(adapter, completeSwipe, null);
```
The 2nd parameter is the left swipe and the 3rd is the right swipe. Either of these can be `null` if that swipe is not required.

### Filtering
Let's imagine we have an `EditText` with an id of `filter_text`:
```java
EditText filterText = findViewById(R.id.filter_text);
```
...and we define a filter that matches only items containing the filter text:
```java
Filter<Text> searchFilter = text -> text.message.contains(filterText.getText());
```
We can add a text watcher to the `EditText` so that only items matching the search filter are displayed:
```java
filterText.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        view.filter(searchFilter);
    }

    @Override
    public void afterTextChanged(Editable editable) { }
});
```
The filter only affects the displayed results, and not the actual dataset.