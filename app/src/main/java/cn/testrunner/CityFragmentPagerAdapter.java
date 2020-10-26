package cn.testrunner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class CityFragmentPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList;

    public CityFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {

        return fragmentList.size();
    }

    int childCount = 0;  //标识ViewPager包含的页数
    //当ViewPager的页数发生变化时,必须要重写两个函数

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (childCount > 0) {
            childCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        this.childCount = getCount();
        super.notifyDataSetChanged();

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
}
