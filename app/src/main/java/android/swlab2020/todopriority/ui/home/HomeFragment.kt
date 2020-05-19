package android.swlab2020.todopriority.ui.home

import android.os.Bundle
import android.swlab2020.todopriority.R
import android.swlab2020.todopriority.resources.SelectType
import android.swlab2020.todopriority.resources.setDropdownMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_home, container, false)

//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDropdownMenu(
            requireContext(),
            home_dropdown_left as AutoCompleteTextView,
            SelectType.PROJECT,
            getString(R.string.select_all)
        )
        setDropdownMenu(
            requireContext(),
            home_dropdown_right as AutoCompleteTextView,
            SelectType.SORT
        )
    }
}
