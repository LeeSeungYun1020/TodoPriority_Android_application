package android.swlab2020.todopriority.ui.analyze

import android.os.Bundle
import android.swlab2020.todopriority.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class AnalyzeFragment : Fragment() {

    companion object {
        fun newInstance() = AnalyzeFragment()
    }

    private lateinit var analyzeViewModel: AnalyzeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        analyzeViewModel = ViewModelProvider(this)[AnalyzeViewModel::class.java]
        val root = inflater.inflate(R.layout.fragment_analyze, container, false)
        val textView: TextView = root.findViewById(R.id.text_analyze)
        analyzeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        analyzeViewModel = ViewModelProvider(this)[AnalyzeViewModel::class.java]
    }

}
