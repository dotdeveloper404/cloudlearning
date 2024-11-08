package com.sparkmembership.sparkowner.presentation.ui.invoice.InvoiceHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparkmembership.sparkfitness.util.DateUtil.UTC_DATE_FORMAT_3
import com.sparkmembership.sparkfitness.util.toVisible
import com.sparkmembership.sparkowner.R
import com.sparkmembership.sparkowner.adapter.InvoiceHistoryAdapter
import com.sparkmembership.sparkowner.data.remote.Resource
import com.sparkmembership.sparkowner.data.response.Contact
import com.sparkmembership.sparkowner.data.response.InvoiceHistory
import com.sparkmembership.sparkowner.databinding.FragmentInvociesBinding
import com.sparkmembership.sparkowner.presentation.components.getFirstAndLastDayOfMonth
import com.sparkmembership.sparkowner.presentation.ui.MainActivity
import com.sparkmembership.sparkowner.presentation.ui.invoice.InvoiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InvoicesFragment : Fragment(), InvoicesFilterBottomSheetFragment.OnButtonClickListener,
    InvoiceHistoryAdapter.OnInvoiceItem {

    lateinit var binding: FragmentInvociesBinding
    private val invoiceViewModel by viewModels<InvoiceViewModel>()
    val invoiceHistory = InvoiceHistoryData()
    var invoiceHistoryAdapter : InvoiceHistoryAdapter? = null
    var invoiceHistoryList = ArrayList<InvoiceHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvociesBinding.inflate(inflater, container, false)
        setToolBar()
        getInvoiceHistory()
        initRecyclerView()
        observerViewModel()
        return binding.root
    }

    private fun observerViewModel() {
        invoiceViewModel.invoiceHistory.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.includedProgressLayout.loaderView.visibility = View.VISIBLE
                    binding.notFoundDataLayout.visibility = View.GONE
                }

                is Resource.Success -> {
                    invoiceHistoryList.clear()
                    if (it.data?.result?.isNotEmpty() == true){
                        invoiceHistoryList.addAll(it.data.result)
                        invoiceHistoryAdapter?.setTimeSlipHistory(invoiceHistoryList)
                    }else{
                        binding.notFoundDataLayout.toVisible()
                    }
                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                }

                is Resource.Error -> {
                    binding.notFoundDataLayout.visibility =
                        View.VISIBLE
                    binding.includedProgressLayout.loaderView.visibility = View.GONE
                }
            }
        }
    }

    private fun getInvoiceHistory() {
        val (firstDay, lastDay) = getFirstAndLastDayOfMonth(UTC_DATE_FORMAT_3)
        invoiceHistory.startDate = firstDay
        invoiceHistory.endDate = lastDay
        lifecycleScope.launch {
            invoiceViewModel.getInvoiceHistory(startDate = invoiceHistory.startDate, endDate = invoiceHistory.endDate)
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        invoiceHistoryAdapter = InvoiceHistoryAdapter(requireContext(),this)
        binding.recyclerView.adapter = invoiceHistoryAdapter
    }

    private fun setToolBar() {
        (activity as MainActivity).showToolbar()

        (activity as? MainActivity)?.initializeCustomToolbar(
            title = getString(R.string.invoices),
            toolbarColor = android.R.color.transparent,
            showBackButton = false,
            icons = listOf(
                R.drawable.icon_filter to {
                    val bottomSheetFragment =
                        InvoicesFilterBottomSheetFragment(invoiceHistory)
                    bottomSheetFragment.setListener(this)
                    bottomSheetFragment.show(parentFragmentManager, "AllNotesFilterBottomSheetFragment")
                }
            ),
            onBackPress = {
            }
        )

    }

    override fun onApplyFilter() {
        lifecycleScope.launch {
            invoiceViewModel.getInvoiceHistory(startDate = invoiceHistory.startDate, endDate = invoiceHistory.endDate, invoiceType = invoiceHistory.invoiceType, contactId = invoiceHistory.contact?.contactID)
        }
    }

    override fun onResetFilter() {
    }

    data class InvoiceHistoryData(
        var contact: Contact ?= null,
        var invoiceType: Int?= null,
        var startDate: String = "",
        var endDate: String = "",
    )

    override fun onClick(invoiceHistoryDto: InvoiceHistory) {
        val bundle = Bundle().apply {
            putInt("invoiceId", invoiceHistoryDto.invoiceID)
        }
        findNavController().navigate(R.id.invoiceDetailFragment, bundle)
    }


}