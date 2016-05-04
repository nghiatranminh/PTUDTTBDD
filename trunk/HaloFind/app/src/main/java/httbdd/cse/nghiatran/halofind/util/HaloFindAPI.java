package httbdd.cse.nghiatran.halofind.util;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import httbdd.cse.nghiatran.halofind.Model.Category;
import httbdd.cse.nghiatran.halofind.Model.District;
import httbdd.cse.nghiatran.halofind.Model.Food;
import httbdd.cse.nghiatran.halofind.Model.User;


/**
 * Created by TranMinhNghia_512023 on 4/12/2016.
 */
public class HaloFindAPI {
    public static void getDatasnapshotFood(final UpdateUIsHandler update) {
        Firebase myFirebaseRef = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.FOOD + "/");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Food> foods = new ArrayList<Food>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    foods.add(data.getValue(Food.class));
                }
                update.onSuccessAction(foods, HaloFindHelper.FOOD);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
    }

    public static void getDatasnapshotCategory(final UpdateUIsHandler update) {
        Firebase myFirebaseRef = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.CATEGORY + "/");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Category> categories = new ArrayList<Category>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    categories.add(data.getValue(Category.class));
                }
                update.onSuccessAction(categories, HaloFindHelper.CATEGORY);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
    }

    public static void getDatasnapshotDistrict(final UpdateUIsHandler update) {
        Firebase myFirebaseRef = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.DISTRICT + "/");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<District> districts = new ArrayList<District>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    districts.add(data.getValue(District.class));
                }
                update.onSuccessAction(districts, HaloFindHelper.DISTRICT);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
    }

    public static void getDatasnapshotUsers(final UpdateUIsHandler update) {
        Firebase myFirebaseRef = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.USERS + "/");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<User>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    users.add(data.getValue(User.class));
                }
                update.onSuccessAction(users, HaloFindHelper.USERS);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public static void register(final String name, final String image, final String pass, final String email, final int a, final UpdateUIsHandler update) {
        Firebase myFire = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.USERS + "/");
        Firebase fbuser = myFire.child(Long.toString(a));
        User newuser = new User(email, name, image, pass);
        fbuser.setValue(newuser);
    }

    public static void getAllUser(final UpdateUIsHandler update) {
        Firebase myFirebaseRef = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.USERS + "/");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<User>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    users.add(data.getValue(User.class));
                }
                update.onSuccessAction(users, HaloFindHelper.USERS);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
              }

        });
    }

    public static void getAllFood(final UpdateUIsHandler update) {
        Firebase myFirebaseRef = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.FOOD + "/");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Food> foods = new ArrayList<Food>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    foods.add(data.getValue(Food.class));
                }
                update.onSuccessAction(foods, HaloFindHelper.FOOD);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });


    }

    public static void upload(final String name, final String image, final String pass, final String email, final int a, final UpdateUIsHandler update) {
        Firebase myFire = new Firebase(HaloFindHelper.serverUrl + HaloFindHelper.USERS + "/");
        Firebase fbuser = myFire.child(Long.toString(a));
        User newuser = new User(email, name, image, pass);
        fbuser.setValue(newuser);
    }
}