/**
 * Copyright (c) 2015 FeedHenry Ltd, All Rights Reserved.
 *
 * Please refer to your contract with FeedHenry for the software license agreement.
 * If you do not have a contract, you do not have a license to use this software.
 */
package org.feedhenry.apps.arthenry.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import org.feedhenry.apps.arthenry.util.adapter.ProjectViewAdapter;
import org.feedhenry.apps.arthenry.vo.Project;

public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final OnItemSwipeListener listener;

    public SwipeTouchHelper(OnItemSwipeListener listener) {
        super(0, ItemTouchHelper.RIGHT);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        ProjectViewAdapter.ProjectViewHolder v =
                (ProjectViewAdapter.ProjectViewHolder) viewHolder;
        listener.onItemSwipe(v.getItem());
    }

    public interface OnItemSwipeListener {
        public void onItemSwipe(Project item);
    }

}
